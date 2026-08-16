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
                "Preserve the original meaning and tone exactly — only fix errors, don't rewrite style. " +
                "Return ONLY the corrected text, nothing else:\n\n$text"
        "explain" -> "Explain what the following sentence means, in simple plain language a beginner " +
                "would understand. Keep it short, 1-2 sentences, no jargon:\n\n$text"
        "cv" -> "Rewrite the following text so it sounds professional, achievement-focused, and polished " +
                "enough for a CV, resume, or job application. Use strong, active language and remove " +
                "filler words. Return ONLY the rewritten text:\n\n$text"
        "business" -> "Rewrite the following text in a clear, confident, respectful business/workplace " +
                "tone suitable for a professional email or report. Be direct but courteous, and remove " +
                "unnecessary casual phrasing. Return ONLY the rewritten text:\n\n$text"
        "livecheck" -> "TASK: single-word spell check. INPUT is exactly one word, possibly misspelled " +
                "or possibly incomplete/an abbreviation. " +
                "RULES: Output EXACTLY ONE WORD. Never output a sentence, phrase, or multiple words. " +
                "Never output punctuation. Never explain. If the input word is a real, correctly-spelled " +
                "English word already (including short/common words like 'ho', 'ok', 'go', 'hi'), output " +
                "it back UNCHANGED. Only output a different single word if the input is clearly a typo " +
                "of a common English word (like 'wil' -> 'will', 'bac' -> 'back', 'schol' -> 'school'). " +
                "OUTPUT FORMAT: exactly one lowercase or as-cased word, nothing else, no period, no quotes.\n\n" +
                "INPUT: $text\nOUTPUT:"
        else -> "Rewrite the following text in a $task tone. Return ONLY the rewritten text:\n\n$text"
    }

    private fun toneInstruction(tone: String): String = when (tone) {
        "professional" -> "Rewrite this in a professional, polished tone suitable for work or business " +
                "communication. Use clear, precise language. Avoid slang and casual phrasing."
        "friendly" -> "Rewrite this in a warm, friendly, approachable tone, like talking to someone you " +
                "like and trust. Keep it natural, not overly formal."
        "casual" -> "Rewrite this in a relaxed, casual, conversational tone, like texting a friend. " +
                "Contractions and informal phrasing are fine."
        "formal" -> "Rewrite this in a formal, respectful tone suitable for official or serious " +
                "correspondence. Avoid contractions and slang entirely."
        "funny" -> "Rewrite this to be genuinely funny and light-hearted, adding humor or a playful twist " +
                "while keeping the core message intact."
        "flirty" -> "Rewrite this with a playful, flirty, charming tone — confident and a little teasing, " +
                "while staying tasteful."
        "polite" -> "Rewrite this to be extra polite and courteous, using considerate, respectful language."
        "confident" -> "Rewrite this to sound confident and assertive, direct and self-assured, without " +
                "being aggressive or rude."
        else -> "Rewrite this in a $tone tone."
    }

    private fun buildMultiPrompt(task: String, text: String): String = when (task) {
        "reply" -> "Suggest 3 short, natural reply options to the following message. Make each option " +
                "genuinely different in approach (e.g. one brief, one warmer, one with a follow-up " +
                "question). Return exactly 3 lines, one reply per line, no numbering, no extra text:\n\n$text"
        "decline" -> "Suggest 3 short, polite ways to decline or say no to the following message. Vary " +
                "the reasoning or warmth between the 3 options. Return exactly 3 lines, one option per " +
                "line, no numbering, no extra text:\n\n$text"
        "shorten" -> "Make the following text shorter and more concise while keeping the core meaning. " +
                "Give 3 versions of increasing brevity (slightly shorter, much shorter, minimal). Return " +
                "exactly 3 lines, one per line, no numbering, no extra text:\n\n$text"
        "expand" -> "Expand the following short text into a fuller, more detailed message, adding " +
                "relevant context or detail. Give 3 different expanded versions. Return exactly 3 lines, " +
                "one per line, no numbering, no extra text:\n\n$text"
        "translate" -> "Translate the following text into Spanish, French, and German, preserving tone " +
                "and meaning as naturally as possible in each language. Return exactly 3 lines in this " +
                "order: Spanish, French, German. No labels, no extra text:\n\n$text"
        else -> {
            val instruction = toneInstruction(task)
            "$instruction Give exactly 3 different versions that vary in wording and phrasing, but keep " +
                    "the same meaning and roughly the same length as the original. Return exactly 3 lines, " +
                    "one version per line, no numbering, no labels, no extra commentary:\n\n$text"
        }
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

    suspend fun run(task: String, text: String): Result<String> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.failure(IllegalArgumentException("Empty text"))
        callApi(buildSinglePrompt(task, text))
    }

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
