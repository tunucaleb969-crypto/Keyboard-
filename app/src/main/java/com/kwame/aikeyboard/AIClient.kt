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

    private fun buildPrompt(task: String, text: String): String = when (task) {
        "grammar" -> "Fix grammar, spelling, punctuation and fluency in the following text. " +
                "Return ONLY the corrected text, nothing else:\n\n$text"
        "reply" -> "Suggest 3 short, natural reply options to the following message. " +
                "Return ONLY the 3 replies, one per line, no numbering:\n\n$text"
        else -> "Rewrite the following text in a $task tone. Keep the same meaning and length " +
                "roughly the same. Return ONLY the rewritten text, nothing else:\n\n$text"
    }

    suspend fun run(task: String, text: String): Result<String> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.failure(IllegalArgumentException("Empty text"))
        try {
            val body = JSONObject().apply {
                put("model", "meta/llama-3.1-8b-instruct")
                put("messages", JSONArray().put(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", buildPrompt(task, text))
                    }
                ))
                put("max_tokens", 400)
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
                    return@withContext Result.failure(Exception("API error ${response.code}: $raw"))
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
}
