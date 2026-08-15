package com.kwame.aikeyboard

import android.content.Context

object Prefs {
    private const val FILE = "ai_keyboard_prefs"
    private const val KEY_API = "api_key"
    private const val KEY_SOUND = "key_sound"
    private const val KEY_VIBRATE = "key_vibrate"
    private const val KEY_AUTOCAP = "auto_capitalize"
    private const val KEY_HEIGHT = "keyboard_height"
    private const val KEY_CLIPS = "clip_history"
    private const val CLIP_DIVIDER = "\u0001"
    private const val MAX_CLIPS = 8
    private const val KEY_WORD_SUGGEST = "word_suggestions_enabled"
    private const val KEY_AUTOCORRECT = "auto_correct_enabled"

    fun getApiKey(context: Context): String =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_API, "") ?: ""

    fun setApiKey(context: Context, value: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_API, value).apply()
    }

    fun getSoundEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_SOUND, true)

    fun setSoundEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_SOUND, value).apply()
    }

    fun getVibrateEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_VIBRATE, true)

    fun setVibrateEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_VIBRATE, value).apply()
    }

    fun getAutoCapitalize(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_AUTOCAP, true)

    fun setAutoCapitalize(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUTOCAP, value).apply()
    }

    fun getKeyboardHeight(context: Context): Int =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY_HEIGHT, 1)

    fun setKeyboardHeight(context: Context, value: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_HEIGHT, value).apply()
    }

    fun getClipHistory(context: Context): List<String> {
        val raw = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_CLIPS, "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split(CLIP_DIVIDER).filter { it.isNotBlank() }
    }

    fun addClip(context: Context, text: String) {
        if (text.isBlank()) return
        val current = getClipHistory(context).toMutableList()
        current.remove(text)
        current.add(0, text)
        val trimmed = current.take(MAX_CLIPS)
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_CLIPS, trimmed.joinToString(CLIP_DIVIDER)).apply()
    }

    fun clearClips(context: Context) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().remove(KEY_CLIPS).apply()
    }

    fun getWordSuggestionsEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_WORD_SUGGEST, true)

    fun setWordSuggestionsEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_WORD_SUGGEST, value).apply()
    }

    fun getAutoCorrectEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_AUTOCORRECT, true)

    fun setAutoCorrectEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUTOCORRECT, value).apply()
    }
}
