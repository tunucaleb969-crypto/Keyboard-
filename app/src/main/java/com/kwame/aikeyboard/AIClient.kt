package com.kwame.aikeyboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AIClient(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val jsonMedia = "application/json".toMediaType()

    private fun buildSinglePrompt(task: String, text: String): String = when (task) {
        "grammar" -> "Fix grammar, spelling, punctuation and fluency in the following text. " +
                "Return ONLY the corrected text, nothing else:\n\n$text"
        "explain" -> "Explain what the following sentence means, in simple plain language. " +
                "Keep it short, 1-2 sentences:\n\n$text"
        "cv" -> "Rewrite the following text so it sounds professional and polished enough for a CV, " +
                "resume, or job application. Return ONLY the rewritten text:\n\n$text"
        "business" -> "Rewrite the following text in a clear, confident business/workplace tone, " +
                "suitable for a professional email or report. Return ONLY the rewritten text:\n\n$text"
        else -> "Rewrite the following text in a $task tone. Return ONLY the rewritten text:\n\n$text"
    }

    private fun buildMultiPrompt(task: String, text: String): String = when (task) {
        "reply" -> "Suggest 3 short, natural reply options to the following message. " +
                "Return exactly 3 lines, one reply per line, no numbering, no extra text:\n\n$text"
        else -> "Rewrite the following text in a $task tone. Give exactly 3 different alternative " +
                "versions. Return exactly 3 lines, one version per line, no numbering, no labels, " +
                "no extra commentary:\n\n$text"
    }

    private fun callApi(prompt: String): Result<String> {
        return try {
            val body = JSONObject().apply {
                put("model", "meta/llama-3.1-8b-instruct")
                put("messages", JSONArray().put(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    }
                ))
                put("max_tokens", 500)
            }

            val request = Request.Builder()
                .url("https://integrate.api.nvidia.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("content-type", "application/json")
                .post(body.toString().toRequestBody(jsonMedia))
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return Result.failure(Exception("API error ${response.code}: $raw"))
                }
                val json = JSONObject(raw)
                val message = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                Result.success(message.trim())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Single-result tasks: grammar fix, explain, CV mode, business mode. */
    suspend fun run(task: String, text: String): Result<String> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.failure(IllegalArgumentException("Empty text"))
        callApi(buildSinglePrompt(task, text))
    }

    /** Multi-result tasks: tone rewrites and reply suggestions, returned as up to 3 options. */
    suspend fun runMulti(task: String, text: String): Result<List<String>> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.failure(IllegalArgumentException("Empty text"))
        callApi(buildMultiPrompt(task, text)).map { raw ->
            raw.lines()
                .map { it.trim().trimStart('-', '•', '*', ' ') }
                .filter { it.isNotBlank() }
                .take(3)
        }
    }
}
