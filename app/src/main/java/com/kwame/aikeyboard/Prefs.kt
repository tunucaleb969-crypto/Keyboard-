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
    private const val KEY_UNDO_AUTOCORRECT = "undo_autocorrect_enabled"
    private const val KEY_DOUBLE_SPACE_PERIOD = "double_space_period_enabled"
    private const val KEY_AUTO_SPACE_PUNCT = "auto_space_punct_enabled"
    private const val KEY_REMEMBER_CAPS = "remember_caps_enabled"
    private const val KEY_CLIPBOARD_SUGGEST = "clipboard_suggestions_enabled"
    private const val KEY_VIBRATE_DURATION = "vibrate_duration_ms"
    private const val KEY_SOUND_VOLUME = "sound_volume"
    private const val KEY_LONGPRESS_SOUND = "longpress_sound_enabled"
    private const val KEY_REPEATED_VIBRATE = "repeated_vibrate_enabled"

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

    fun getUndoAutocorrectEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_UNDO_AUTOCORRECT, true)

    fun setUndoAutocorrectEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_UNDO_AUTOCORRECT, value).apply()
    }

    fun getDoubleSpacePeriodEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_DOUBLE_SPACE_PERIOD, true)

    fun setDoubleSpacePeriodEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_DOUBLE_SPACE_PERIOD, value).apply()
    }

    fun getAutoSpacePunctuationEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_AUTO_SPACE_PUNCT, false)

    fun setAutoSpacePunctuationEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUTO_SPACE_PUNCT, value).apply()
    }

    fun getRememberCapsEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_REMEMBER_CAPS, false)

    fun setRememberCapsEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_REMEMBER_CAPS, value).apply()
    }

    fun getClipboardSuggestionsEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_CLIPBOARD_SUGGEST, true)

    fun setClipboardSuggestionsEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_CLIPBOARD_SUGGEST, value).apply()
    }

    /** Vibration duration in milliseconds. Default 35ms (matches what we found works on this phone). */
    fun getVibrateDuration(context: Context): Int =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY_VIBRATE_DURATION, 35)

    fun setVibrateDuration(context: Context, value: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_VIBRATE_DURATION, value).apply()
    }

    /** Sound volume 0-100. */
    fun getSoundVolume(context: Context): Int =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY_SOUND_VOLUME, 50)

    fun setSoundVolume(context: Context, value: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_SOUND_VOLUME, value).apply()
    }

    fun getLongPressSoundEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_LONGPRESS_SOUND, false)

    fun setLongPressSoundEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_LONGPRESS_SOUND, value).apply()
    }

    fun getRepeatedVibrateEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_REPEATED_VIBRATE, true)

    fun setRepeatedVibrateEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean(KEY_REPEATED_VIBRATE, value).apply()
    }
}
