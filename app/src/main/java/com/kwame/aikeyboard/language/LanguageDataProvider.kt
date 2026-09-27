package com.kwame.aikeyboard.language

/** A single dictionary word available for autocomplete/spellcheck, with its usage frequency. */
data class WordEntry(
    val word: String,
    val frequency: Int,
    val category: String
)

/** A bundled or learned word-pair used for next-word prediction. */
data class BigramEntry(
    val previousWord: String,
    val nextWord: String,
    val frequency: Int
)

/**
 * Abstraction over however the keyboard's bundled language data is actually stored.
 * The rest of the keyboard (WordSuggester, NextWordPredictor, ContractionExpander, the
 * upcoming SpellCorrector) talks only to this interface — never directly to SQLite —
 * so the storage engine can later be swapped for a trie/native dictionary without
 * touching any of those files. See SqliteLanguageDataProvider for the current
 * (Phase 1) implementation.
 */
interface LanguageDataProvider {
    /** Bundled words whose normalized form starts with [prefix], best-frequency first, capped at [limit]. */
    fun wordsStartingWith(prefix: String, limit: Int = 20): List<WordEntry>

    /** True if [word] (case-insensitive) exists in the bundled dictionary. */
    fun isKnownWord(word: String): Boolean

    /** A fixed bundled correction for [misspelling] (e.g. "teh" -> "the"), or null if none is bundled. */
    fun lookupCorrection(misspelling: String): String?

    /** Bundled next-word associations for [previousWord], most-frequent first. */
    fun bigramsFor(previousWord: String, limit: Int = 5): List<BigramEntry>

    /** Bundled emoji suggestions for [word]. */
    fun emojisFor(word: String): List<String>

    /** Closes the underlying storage. Call when the keyboard service is destroyed. */
    fun close()
}
