package com.kwame.aikeyboard

import android.content.Context

object Prefs {
    private const val FILE = "ai_keyboard_prefs"
    private const val KEY_API = "api_key"
    private const val KEY_SOUND = "key_sound"
    private const val KEY_VIBRATE = "key_vibrate"
    private const val KEY_AUTOCAP = "auto_capitalize"
    private const val KEY_HEIGHT = "keyboard_height"

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

    /** 0 = small, 1 = medium (default), 2 = large */
    fun getKeyboardHeight(context: Context): Int =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY_HEIGHT, 1)

    fun setKeyboardHeight(context: Context, value: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY_HEIGHT, value).apply()
    }
}
