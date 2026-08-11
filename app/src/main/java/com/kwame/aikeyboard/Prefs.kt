package com.kwame.aikeyboard

import android.content.Context

object Prefs {
    private const val FILE = "ai_keyboard_prefs"
    private const val KEY_API = "api_key"

    fun getApiKey(context: Context): String =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_API, "") ?: ""

    fun setApiKey(context: Context, value: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_API, value).apply()
    }
}
