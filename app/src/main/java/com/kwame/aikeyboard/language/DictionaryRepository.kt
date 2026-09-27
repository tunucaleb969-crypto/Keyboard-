package com.kwame.aikeyboard.language

import android.content.Context

/**
 * App-wide access point for language data. WordSuggester, NextWordPredictor,
 * ContractionExpander, and the upcoming SpellCorrector should all go through this
 * object instead of constructing a LanguageDataProvider directly — so swapping the
 * underlying implementation later (e.g. to a trie/native dictionary) is a one-line
 * change here rather than a change everywhere it's used.
 */
object DictionaryRepository {
    @Volatile private var provider: LanguageDataProvider? = null

    fun get(context: Context): LanguageDataProvider {
        return provider ?: synchronized(this) {
            provider ?: SqliteLanguageDataProvider(context.applicationContext).also { provider = it }
        }
    }
}
