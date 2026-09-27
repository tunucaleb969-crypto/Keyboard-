package com.kwame.aikeyboard.language

import android.content.Context

/**
 * The current (Phase 1) implementation of [LanguageDataProvider], backed by the
 * on-device SQLite database created by [DictionaryDbHelper]. This is the only class
 * that should import android.database.sqlite.* for language data — everything else in
 * the keyboard talks to the [LanguageDataProvider] interface instead, so this class can
 * be swapped for a trie/native implementation later without touching callers.
 */
class SqliteLanguageDataProvider(context: Context) : LanguageDataProvider {

    private val dbHelper = DictionaryDbHelper(context)

    /** Escapes SQLite LIKE wildcards in user-typed text before it's used in a LIKE pattern. */
    private fun escapeLikePattern(input: String): String =
        input.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

    override fun wordsStartingWith(prefix: String, limit: Int): List<WordEntry> {
        if (prefix.isBlank()) return emptyList()
        val pattern = escapeLikePattern(prefix.lowercase()) + "%"
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT word, frequency, category FROM words " +
                "WHERE normalized LIKE ? ESCAPE '\\' ORDER BY frequency DESC LIMIT ?",
            arrayOf(pattern, limit.toString())
        )
        val results = mutableListOf<WordEntry>()
        cursor.use {
            while (it.moveToNext()) {
                results.add(WordEntry(word = it.getString(0), frequency = it.getInt(1), category = it.getString(2)))
            }
        }
        return results
    }

    override fun isKnownWord(word: String): Boolean {
        if (word.isBlank()) return false
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM words WHERE normalized = ? LIMIT 1", arrayOf(word.lowercase()))
        return cursor.use { it.moveToFirst() }
    }

    override fun lookupCorrection(misspelling: String): String? {
        if (misspelling.isBlank()) return null
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT correction FROM corrections WHERE misspelling = ? LIMIT 1",
            arrayOf(misspelling.lowercase())
        )
        return cursor.use { if (it.moveToFirst()) it.getString(0) else null }
    }

    override fun bigramsFor(previousWord: String, limit: Int): List<BigramEntry> {
        if (previousWord.isBlank()) return emptyList()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT previous_word, next_word, frequency FROM bigrams " +
                "WHERE previous_word = ? ORDER BY frequency DESC LIMIT ?",
            arrayOf(previousWord.lowercase(), limit.toString())
        )
        val results = mutableListOf<BigramEntry>()
        cursor.use {
            while (it.moveToNext()) {
                results.add(BigramEntry(previousWord = it.getString(0), nextWord = it.getString(1), frequency = it.getInt(2)))
            }
        }
        return results
    }

    override fun emojisFor(word: String): List<String> {
        if (word.isBlank()) return emptyList()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT emoji FROM emoji_associations WHERE word = ?", arrayOf(word.lowercase()))
        val results = mutableListOf<String>()
        cursor.use {
            while (it.moveToNext()) results.add(it.getString(0))
        }
        return results
    }

    override fun close() {
        dbHelper.close()
    }
}
