package com.kwame.aikeyboard.language

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Creates and upgrades the bundled language database. Schema version is tracked via
 * SQLiteOpenHelper's own version number; content updates (new words, corrected
 * frequencies) are tracked separately in update_metadata so a future update system can
 * tell "new schema" apart from "new data, same schema".
 *
 * Word categories (names/places/technical terms/abbreviations/slang) are kept as a
 * plain column on `words` rather than separate tables. This is a deliberate choice:
 * a single indexed prefix query across all categories at once is far faster on a
 * low-end phone than a multi-table UNION on every keystroke, and it avoids the
 * "unnecessary tables" pitfall directly.
 */
class DictionaryDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_SCHEMA_VERSION) {

    companion object {
        const val DB_NAME = "language_data.db"
        const val DB_SCHEMA_VERSION = 1

        const val CATEGORY_COMMON = "common"
        const val CATEGORY_NAME = "name"
        const val CATEGORY_PLACE = "place"
        const val CATEGORY_TECHNICAL = "technical"
        const val CATEGORY_ABBREVIATION = "abbreviation"
        const val CATEGORY_SLANG = "slang"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE words (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                word TEXT NOT NULL,
                normalized TEXT NOT NULL,
                category TEXT NOT NULL,
                frequency INTEGER NOT NULL DEFAULT 0,
                region TEXT,
                UNIQUE(normalized, category)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_words_normalized ON words(normalized)")
        db.execSQL("CREATE INDEX idx_words_category ON words(category)")

        db.execSQL(
            """
            CREATE TABLE phrases (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                phrase TEXT NOT NULL,
                normalized TEXT NOT NULL UNIQUE,
                frequency INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_phrases_normalized ON phrases(normalized)")

        db.execSQL(
            """
            CREATE TABLE corrections (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                misspelling TEXT NOT NULL UNIQUE,
                correction TEXT NOT NULL,
                confidence REAL NOT NULL DEFAULT 1.0
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_corrections_misspelling ON corrections(misspelling)")

        db.execSQL(
            """
            CREATE TABLE bigrams (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                previous_word TEXT NOT NULL,
                next_word TEXT NOT NULL,
                frequency INTEGER NOT NULL DEFAULT 1,
                UNIQUE(previous_word, next_word)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_bigrams_previous ON bigrams(previous_word)")

        db.execSQL(
            """
            CREATE TABLE emoji_associations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                word TEXT NOT NULL,
                emoji TEXT NOT NULL,
                UNIQUE(word, emoji)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_emoji_word ON emoji_associations(word)")

        db.execSQL(
            """
            CREATE TABLE update_metadata (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("INSERT INTO update_metadata (key, value) VALUES ('data_version', '0')")
        db.execSQL("INSERT INTO update_metadata (key, value) VALUES ('schema_version', '$DB_SCHEMA_VERSION')")

        // Phase 1 intentionally seeds no rows yet. Seed data (migrated from
        // WordSuggester's existing list, plus new license-verified vocabulary/places)
        // lands in Phase 3, once SOURCES.md documents exactly where each row came from.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No schema migrations yet (this is schema version 1). Future versions should
        // ALTER/CREATE only what changed, never DROP existing tables without a
        // data-preserving migration path, since that would silently wipe learned data.
    }
}
