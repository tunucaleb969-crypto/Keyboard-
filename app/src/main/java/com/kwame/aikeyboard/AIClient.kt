package com.kwame.aikeyboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Failure raised by [AIClient]. The message is already written for the person
 * using the keyboard, because the service shows it in a toast.
 */
class AiGatewayException(message: String, val kind: Kind) : Exception(message) {
    enum class Kind { WAKING_UP, UNAUTHORIZED, RATE_LIMITED, PROVIDER, SERVER, OFFLINE, TOO_LONG, OTHER }
}

/**
 * Talks to our own AI gateway (backend/ folder), never to Gemini directly.
 * The Gemini key lives only on the server. The [appKey] here is the gateway's
 * APP_SHARED_SECRET, which the person enters in the keyboard's settings.
 *
 * The prompts now live on the server (backend/app/prompts.py), unchanged.
 */
class AIClient(private val appKey: String) {

    companion object {
        // Public address of the deployed gateway. Not a secret.
        // TODO: replace with the real Render URL after deployment is verified.
        const val GATEWAY_BASE_URL = "https://REPLACE-WITH-RENDER-SERVICE-URL"

        // Must match the max_length in backend/app/schemas.py.
        const val MAX_TEXT_CHARS = 4000

        // After a timeout we assume the free-tier server is asleep. For this long,
        // background per-word spell checks fail instantly instead of each waiting
        // for a timeout. Requests the person triggers on purpose are still tried.
        private const val COLD_WINDOW_MS = 60_000L
        private const val WARM_UP_MIN_GAP_MS = 30_000L

        private val jsonMedia = "application/json".toMediaType()

        // Short on purpose: a keyboard cannot make someone wait a minute.
        private val client = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()

        // Waking a sleeping Render free service takes about a minute, so the
        // background wake-up ping is allowed to wait much longer.
        private val warmUpClient = client.newBuilder()
            .readTimeout(90, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build()

        private val warmUpScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val coldUntilMs = AtomicLong(0L)
        private val lastWarmUpMs = AtomicLong(0L)

        /**
         * Fire-and-forget request to /healthz that wakes a sleeping server.
         * Safe to call often: it does nothing if it ran in the last 30 seconds.
         */
        fun warmUp() {
            val now = System.currentTimeMillis()
            val last = lastWarmUpMs.get()
            if (now - last < WARM_UP_MIN_GAP_MS) return
            if (!lastWarmUpMs.compareAndSet(last, now)) return
            warmUpScope.launch {
                try {
                    val request = Request.Builder().url("$GATEWAY_BASE_URL/healthz").get().build()
                    warmUpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) coldUntilMs.set(0L)
                    }
                } catch (e: Exception) {
                    // Best effort only. The next real request reports any problem.
                }
            }
        }
    }

    private fun wakingUp() = AiGatewayException(
        "AI server is waking up. Try again in about a minute.",
        AiGatewayException.Kind.WAKING_UP
    )

    private fun markCold() {
        coldUntilMs.set(System.currentTimeMillis() + COLD_WINDOW_MS)
        warmUp()
    }

    private fun httpError(code: Int, raw: String): AiGatewayException {
        // Our gateway always answers errors with JSON. An HTML or empty 502/503/504
        // instead comes from Render's proxy while the free service is starting up.
        val fromGateway = raw.trimStart().startsWith("{")
        return when {
            !fromGateway && code in 502..504 -> wakingUp()
            code == 401 -> AiGatewayException(
                "The AI server rejected the key. Check it in the AI Keyboard app.",
                AiGatewayException.Kind.UNAUTHORIZED
            )
            code == 429 -> AiGatewayException(
                "Too many AI requests. Wait a moment and try again.",
                AiGatewayException.Kind.RATE_LIMITED
            )
            code == 502 -> AiGatewayException(
                "The AI provider had a problem. Try again.",
                AiGatewayException.Kind.PROVIDER
            )
            code == 503 -> AiGatewayException(
                "The AI server is not set up correctly.",
                AiGatewayException.Kind.SERVER
            )
            else -> AiGatewayException(
                "AI request failed (error $code).",
                AiGatewayException.Kind.OTHER
            )
        }
    }

    private fun post(path: String, task: String, text: String): Result<JSONObject> {
        if (task == "livecheck" && System.currentTimeMillis() < coldUntilMs.get()) {
            return Result.failure(wakingUp())
        }
        return try {
            val body = JSONObject().put("task", task).put("text", text)
            val request = Request.Builder()
                .url("$GATEWAY_BASE_URL/api/v1/$path")
                .addHeader("X-App-Key", appKey)
                .addHeader("content-type", "application/json")
                .post(body.toString().toRequestBody(jsonMedia))
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val error = httpError(response.code, raw)
                    if (error.kind == AiGatewayException.Kind.WAKING_UP) markCold()
                    return Result.failure(error)
                }
                coldUntilMs.set(0L)
                Result.success(JSONObject(raw))
            }
        } catch (e: InterruptedIOException) {
            // Covers connect, read and whole-call timeouts: the free server is most likely asleep.
            markCold()
            Result.failure(wakingUp())
        } catch (e: IOException) {
            Result.failure(
                AiGatewayException(
                    "Couldn't reach the AI server. Check your connection.",
                    AiGatewayException.Kind.OFFLINE
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun tooLong() = AiGatewayException(
        "That text is too long for AI (limit $MAX_TEXT_CHARS characters).",
        AiGatewayException.Kind.TOO_LONG
    )

    suspend fun run(task: String, text: String): Result<String> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.failure(IllegalArgumentException("Empty text"))
        if (text.length > MAX_TEXT_CHARS) return@withContext Result.failure(tooLong())
        post("complete", task, text).mapCatching { it.getString("result").trim() }
    }

    suspend fun runMulti(task: String, text: String): Result<List<String>> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.failure(IllegalArgumentException("Empty text"))
        if (text.length > MAX_TEXT_CHARS) return@withContext Result.failure(tooLong())
        post("suggest", task, text).mapCatching { json ->
            val results = json.getJSONArray("results")
            (0 until results.length())
                .map { results.getString(it).trim() }
                .filter { it.isNotBlank() }
                .take(3)
        }
    }
}
