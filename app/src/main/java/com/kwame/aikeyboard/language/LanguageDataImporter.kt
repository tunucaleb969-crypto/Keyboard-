package com.kwame.aikeyboard.language

import android.content.Context
import android.content.res.AssetManager
import android.database.sqlite.SQLiteDatabase
import java.io.BufferedReader
import java.io.IOException
import java.security.MessageDigest

/**
 * Reads language-data source files from assets/language_import/, validates each line
 * with [LanguageDataValidator], and inserts valid rows into the SQLite database created
 * by [DictionaryDbHelper]. Safe to call on every app start: each file's exact content is
 * hashed (SHA-256) and recorded in `update_metadata`, so an unchanged file is skipped
 * entirely and only new/changed files are re-processed — this is what makes updates
 * incremental instead of a full rebuild every time.
 *
 * This does file I/O and batched database writes, so it must be called off the main
 * thread — AIKeyboardService launches it on Dispatchers.IO.
 */
object LanguageDataImporter {

    private const val IMPORT_DIR = "language_import"
    private const val WORDS_FILE = "words_seed.tsv"
    private const val CORRECTIONS_FILE = "corrections_seed.tsv"
    private const val BIGRAMS_FILE = "bigrams_seed.tsv"
    private const val EMOJI_FILE = "emoji_seed.tsv"
    private const val PHRASES_FILE = "phrases_seed.tsv"

    /** Runs the full import. Safe to call repeatedly — unchanged files are skipped. */
    fun importAll(context: Context): ImportSummary {
        val dbHelper = DictionaryDbHelper(context)
        val db = dbHelper.writableDatabase
        val results = mutableListOf<ImportFileResult>()
        try {
            results.add(importWords(context.assets, db, WORDS_FILE))
            results.add(importCorrections(context.assets, db, CORRECTIONS_FILE))
            results.add(importBigrams(context.assets, db, BIGRAMS_FILE))
            results.add(importEmoji(context.assets, db, EMOJI_FILE))
            results.add(importPhrases(context.assets, db, PHRASES_FILE))
        } finally {
            dbHelper.close()
        }
        return ImportSummary(results)
    }

    private fun readAssetLines(assets: AssetManager, fileName: String): List<String>? {
        return try {
            assets.open("$IMPORT_DIR/$fileName").bufferedReader().use(BufferedReader::readLines)
        } catch (e: IOException) {
            null
        }
    }

    private fun contentHash(lines: List<String>): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(lines.joinToString("\n").toByteArray(Charsets.UTF_8))
        // Mask each byte to 0..255 before formatting — Kotlin's Byte is signed, and
        // "%02x".format on a negative Byte without masking sign-extends to 8 hex digits
        // instead of 2 (a real bug caught during review, not a hypothetical one).
        return digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xFF) }
    }

    private fun metadataKeyFor(fileName: String) = "imported_hash:$fileName"

    private fun alreadyImported(db: SQLiteDatabase, fileName: String, hash: String): Boolean {
        val cursor = db.rawQuery("SELECT value FROM update_metadata WHERE key = ?", arrayOf(metadataKeyFor(fileName)))
        val storedHash = cursor.use { if (it.moveToFirst()) it.getString(0) else null }
        return storedHash == hash
    }

    private fun recordImported(db: SQLiteDatabase, fileName: String, hash: String) {
        db.execSQL(
            "INSERT OR REPLACE INTO update_metadata (key, value) VALUES (?, ?)",
            arrayOf(metadataKeyFor(fileName), hash)
        )
    }

    private fun skippedResult(fileName: String) =
        ImportFileResult(fileName, 0, 0, 0, 0, 0, emptyList(), skippedUnchanged = false)

    private fun unchangedResult(fileName: String, lineCount: Int) =
        ImportFileResult(fileName, lineCount, 0, 0, 0, 0, emptyList(), skippedUnchanged = true)

    private fun importWords(assets: AssetManager, db: SQLiteDatabase, fileName: String): ImportFileResult {
        val lines = readAssetLines(assets, fileName) ?: return skippedResult(fileName)
        val hash = contentHash(lines)
        if (alreadyImported(db, fileName, hash)) return unchangedResult(fileName, lines.size)

        var inserted = 0
        var updated = 0
        var duplicateWithinBatch = 0
        val rejections = mutableListOf<RejectedRow>()
        val seenInBatch = mutableSetOf<Pair<String, String>>()

        db.beginTransaction()
        try {
            lines.forEachIndexed { index, rawLine ->
                if (LanguageDataValidator.isSkippableLine(rawLine)) return@forEachIndexed
                LanguageDataValidator.parseWordLine(rawLine).onSuccess { row ->
                    val normalized = row.word.lowercase()
                    val key = normalized to row.category
                    if (!seenInBatch.add(key)) {
                        duplicateWithinBatch++
                        return@onSuccess
                    }
                    val existing = db.rawQuery(
                        "SELECT id FROM words WHERE normalized = ? AND category = ?",
                        arrayOf(normalized, row.category)
                    ).use { it.moveToFirst() }
                    db.execSQL(
                        "INSERT OR REPLACE INTO words (word, normalized, category, frequency, region) VALUES (?, ?, ?, ?, ?)",
                        arrayOf(row.word, normalized, row.category, row.frequency, row.region)
                    )
                    if (existing) updated++ else inserted++
                }.onFailure { e ->
                    rejections.add(RejectedRow(index + 1, rawLine, e.message ?: "invalid row"))
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        recordImported(db, fileName, hash)
        return ImportFileResult(fileName, lines.size, inserted, updated, duplicateWithinBatch, rejections.size, rejections, skippedUnchanged = false)
    }

    private fun importCorrections(assets: AssetManager, db: SQLiteDatabase, fileName: String): ImportFileResult {
        val lines = readAssetLines(assets, fileName) ?: return skippedResult(fileName)
        val hash = contentHash(lines)
        if (alreadyImported(db, fileName, hash)) return unchangedResult(fileName, lines.size)

        var inserted = 0
        var updated = 0
        val rejections = mutableListOf<RejectedRow>()
        val seenInBatch = mutableSetOf<String>()

        db.beginTransaction()
        try {
            lines.forEachIndexed { index, rawLine ->
                if (LanguageDataValidator.isSkippableLine(rawLine)) return@forEachIndexed
                LanguageDataValidator.parseCorrectionLine(rawLine).onSuccess { row ->
                    val normalized = row.misspelling.lowercase()
                    if (!seenInBatch.add(normalized)) return@onSuccess
                    val existing = db.rawQuery("SELECT id FROM corrections WHERE misspelling = ?", arrayOf(normalized)).use { it.moveToFirst() }
                    db.execSQL(
                        "INSERT OR REPLACE INTO corrections (misspelling, correction, confidence) VALUES (?, ?, ?)",
                        arrayOf(normalized, row.correction, row.confidence)
                    )
                    if (existing) updated++ else inserted++
                }.onFailure { e ->
                    rejections.add(RejectedRow(index + 1, rawLine, e.message ?: "invalid row"))
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        recordImported(db, fileName, hash)
        return ImportFileResult(fileName, lines.size, inserted, updated, 0, rejections.size, rejections, skippedUnchanged = false)
    }

    private fun importBigrams(assets: AssetManager, db: SQLiteDatabase, fileName: String): ImportFileResult {
        val lines = readAssetLines(assets, fileName) ?: return skippedResult(fileName)
        val hash = contentHash(lines)
        if (alreadyImported(db, fileName, hash)) return unchangedResult(fileName, lines.size)

        var inserted = 0
        var updated = 0
        val rejections = mutableListOf<RejectedRow>()
        val seenInBatch = mutableSetOf<Pair<String, String>>()

        db.beginTransaction()
        try {
            lines.forEachIndexed { index, rawLine ->
                if (LanguageDataValidator.isSkippableLine(rawLine)) return@forEachIndexed
                LanguageDataValidator.parseBigramLine(rawLine).onSuccess { row ->
                    val prev = row.previousWord.lowercase()
                    val next = row.nextWord.lowercase()
                    if (!seenInBatch.add(prev to next)) return@onSuccess
                    val existing = db.rawQuery(
                        "SELECT id FROM bigrams WHERE previous_word = ? AND next_word = ?",
                        arrayOf(prev, next)
                    ).use { it.moveToFirst() }
                    db.execSQL(
                        "INSERT OR REPLACE INTO bigrams (previous_word, next_word, frequency) VALUES (?, ?, ?)",
                        arrayOf(prev, next, row.frequency)
                    )
                    if (existing) updated++ else inserted++
                }.onFailure { e ->
                    rejections.add(RejectedRow(index + 1, rawLine, e.message ?: "invalid row"))
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        recordImported(db, fileName, hash)
        return ImportFileResult(fileName, lines.size, inserted, updated, 0, rejections.size, rejections, skippedUnchanged = false)
    }

    private fun importEmoji(assets: AssetManager, db: SQLiteDatabase, fileName: String): ImportFileResult {
        val lines = readAssetLines(assets, fileName) ?: return skippedResult(fileName)
        val hash = contentHash(lines)
        if (alreadyImported(db, fileName, hash)) return unchangedResult(fileName, lines.size)

        var inserted = 0
        var duplicate = 0
        val rejections = mutableListOf<RejectedRow>()
        val seenInBatch = mutableSetOf<Pair<String, String>>()

        db.beginTransaction()
        try {
            lines.forEachIndexed { index, rawLine ->
                if (LanguageDataValidator.isSkippableLine(rawLine)) return@forEachIndexed
                LanguageDataValidator.parseEmojiLine(rawLine).onSuccess { row ->
                    val normalized = row.word.lowercase()
                    if (!seenInBatch.add(normalized to row.emoji)) {
                        duplicate++
                        return@onSuccess
                    }
                    db.execSQL(
                        "INSERT OR IGNORE INTO emoji_associations (word, emoji) VALUES (?, ?)",
                        arrayOf(normalized, row.emoji)
                    )
                    inserted++
                }.onFailure { e ->
                    rejections.add(RejectedRow(index + 1, rawLine, e.message ?: "invalid row"))
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        recordImported(db, fileName, hash)
        return ImportFileResult(fileName, lines.size, inserted, 0, duplicate, rejections.size, rejections, skippedUnchanged = false)
    }

    private fun importPhrases(assets: AssetManager, db: SQLiteDatabase, fileName: String): ImportFileResult {
        val lines = readAssetLines(assets, fileName) ?: return skippedResult(fileName)
        val hash = contentHash(lines)
        if (alreadyImported(db, fileName, hash)) return unchangedResult(fileName, lines.size)

        var inserted = 0
        var updated = 0
        val rejections = mutableListOf<RejectedRow>()
        val seenInBatch = mutableSetOf<String>()

        db.beginTransaction()
        try {
            lines.forEachIndexed { index, rawLine ->
                if (LanguageDataValidator.isSkippableLine(rawLine)) return@forEachIndexed
                LanguageDataValidator.parsePhraseLine(rawLine).onSuccess { row ->
                    val normalized = row.phrase.lowercase()
                    if (!seenInBatch.add(normalized)) return@onSuccess
                    val existing = db.rawQuery("SELECT id FROM phrases WHERE normalized = ?", arrayOf(normalized)).use { it.moveToFirst() }
                    db.execSQL(
                        "INSERT OR REPLACE INTO phrases (phrase, normalized, frequency) VALUES (?, ?, ?)",
                        arrayOf(row.phrase, normalized, row.frequency)
                    )
                    if (existing) updated++ else inserted++
                }.onFailure { e ->
                    rejections.add(RejectedRow(index + 1, rawLine, e.message ?: "invalid row"))
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        recordImported(db, fileName, hash)
        return ImportFileResult(fileName, lines.size, inserted, updated, 0, rejections.size, rejections, skippedUnchanged = false)
    }
}
