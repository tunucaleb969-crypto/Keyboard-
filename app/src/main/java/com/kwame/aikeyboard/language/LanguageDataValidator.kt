package com.kwame.aikeyboard.language

/**
 * Validation rules for imported language data. Pure Kotlin, no Android dependency, so
 * these rules can be unit-tested on the JVM without an emulator/device (see
 * LanguageDataValidatorTest.kt).
 */
object LanguageDataValidator {
    private const val MAX_WORD_LENGTH = 45
    private const val MAX_PHRASE_LENGTH = 200

    // Letters (any script), apostrophes, and hyphens only — no spaces, no digits.
    // Multi-word entries belong in the phrases table instead.
    private val WORD_PATTERN = Regex("^[\\p{L}'\\-]+$")

    private val KNOWN_CATEGORIES = setOf(
        DictionaryDbHelper.CATEGORY_COMMON, DictionaryDbHelper.CATEGORY_NAME,
        DictionaryDbHelper.CATEGORY_PLACE, DictionaryDbHelper.CATEGORY_TECHNICAL,
        DictionaryDbHelper.CATEGORY_ABBREVIATION, DictionaryDbHelper.CATEGORY_SLANG
    )

    data class WordRow(val word: String, val frequency: Int, val category: String, val region: String?)
    data class CorrectionRow(val misspelling: String, val correction: String, val confidence: Double)
    data class BigramRow(val previousWord: String, val nextWord: String, val frequency: Int)
    data class EmojiRow(val word: String, val emoji: String)
    data class PhraseRow(val phrase: String, val frequency: Int)

    /** True if [line] should be skipped entirely (blank, or a "#" comment) rather than parsed. */
    fun isSkippableLine(line: String): Boolean {
        val trimmed = line.trim()
        return trimmed.isEmpty() || trimmed.startsWith("#")
    }

    /** Parses and validates one "word<TAB>frequency<TAB>category<TAB>region" line. Region is optional. */
    fun parseWordLine(line: String): Result<WordRow> {
        val cols = line.split("\t")
        if (cols.size < 3) {
            return Result.failure(IllegalArgumentException("Expected at least 3 tab-separated columns (word, frequency, category), got ${cols.size}"))
        }
        val word = cols[0].trim()
        val frequency = cols[1].trim().toIntOrNull()
        val category = cols[2].trim().lowercase()
        val region = cols.getOrNull(3)?.trim()?.takeIf { it.isNotBlank() }

        if (word.isBlank()) return Result.failure(IllegalArgumentException("Empty word"))
        if (word.length > MAX_WORD_LENGTH) return Result.failure(IllegalArgumentException("Word exceeds $MAX_WORD_LENGTH characters"))
        if (!WORD_PATTERN.matches(word)) return Result.failure(IllegalArgumentException("Word contains disallowed characters: \"$word\""))
        if (frequency == null || frequency < 0) return Result.failure(IllegalArgumentException("Invalid frequency: \"${cols.getOrNull(1)}\""))
        if (category !in KNOWN_CATEGORIES) return Result.failure(IllegalArgumentException("Unknown category: \"$category\""))

        return Result.success(WordRow(word, frequency, category, region))
    }

    /** Parses and validates one "misspelling<TAB>correction<TAB>confidence" line. Confidence is optional (default 1.0). */
    fun parseCorrectionLine(line: String): Result<CorrectionRow> {
        val cols = line.split("\t")
        if (cols.size < 2) {
            return Result.failure(IllegalArgumentException("Expected at least 2 tab-separated columns (misspelling, correction), got ${cols.size}"))
        }
        val misspelling = cols[0].trim()
        val correction = cols[1].trim()
        val confidence = cols.getOrNull(2)?.trim()?.toDoubleOrNull() ?: 1.0

        if (misspelling.isBlank() || correction.isBlank()) return Result.failure(IllegalArgumentException("Empty misspelling or correction"))
        if (misspelling.length > MAX_WORD_LENGTH || correction.length > MAX_WORD_LENGTH) {
            return Result.failure(IllegalArgumentException("Entry exceeds $MAX_WORD_LENGTH characters"))
        }
        if (misspelling.equals(correction, ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Misspelling and correction are identical: \"$misspelling\""))
        }
        if (confidence < 0.0 || confidence > 1.0) {
            return Result.failure(IllegalArgumentException("Confidence must be between 0.0 and 1.0, got $confidence"))
        }

        return Result.success(CorrectionRow(misspelling, correction, confidence))
    }

    /** Parses and validates one "previous_word<TAB>next_word<TAB>frequency" line. Frequency is optional (default 1). */
    fun parseBigramLine(line: String): Result<BigramRow> {
        val cols = line.split("\t")
        if (cols.size < 2) {
            return Result.failure(IllegalArgumentException("Expected at least 2 tab-separated columns (previous_word, next_word), got ${cols.size}"))
        }
        val previousWord = cols[0].trim()
        val nextWord = cols[1].trim()
        val frequency = cols.getOrNull(2)?.trim()?.toIntOrNull() ?: 1

        if (previousWord.isBlank() || nextWord.isBlank()) return Result.failure(IllegalArgumentException("Empty previous_word or next_word"))
        if (!WORD_PATTERN.matches(previousWord) || !WORD_PATTERN.matches(nextWord)) {
            return Result.failure(IllegalArgumentException("Disallowed characters in bigram: \"$previousWord\" -> \"$nextWord\""))
        }
        if (frequency < 0) return Result.failure(IllegalArgumentException("Invalid frequency: $frequency"))

        return Result.success(BigramRow(previousWord, nextWord, frequency))
    }

    /** Parses and validates one "word<TAB>emoji" line. */
    fun parseEmojiLine(line: String): Result<EmojiRow> {
        val cols = line.split("\t")
        if (cols.size < 2) return Result.failure(IllegalArgumentException("Expected 2 tab-separated columns (word, emoji), got ${cols.size}"))
        val word = cols[0].trim()
        val emoji = cols[1].trim()

        if (word.isBlank() || emoji.isBlank()) return Result.failure(IllegalArgumentException("Empty word or emoji"))
        if (!WORD_PATTERN.matches(word)) return Result.failure(IllegalArgumentException("Word contains disallowed characters: \"$word\""))
        if (emoji.length > 8) return Result.failure(IllegalArgumentException("\"emoji\" field too long to be a single emoji: \"$emoji\""))

        return Result.success(EmojiRow(word, emoji))
    }

    /** Parses and validates one "phrase<TAB>frequency" line. Frequency is optional (default 0). Must be 2+ words. */
    fun parsePhraseLine(line: String): Result<PhraseRow> {
        val cols = line.split("\t")
        if (cols.isEmpty() || cols[0].isBlank()) return Result.failure(IllegalArgumentException("Empty phrase"))
        val phrase = cols[0].trim()
        val frequency = cols.getOrNull(1)?.trim()?.toIntOrNull() ?: 0

        if (phrase.length > MAX_PHRASE_LENGTH) return Result.failure(IllegalArgumentException("Phrase exceeds $MAX_PHRASE_LENGTH characters"))
        if (phrase.split(" ").filter { it.isNotBlank() }.size < 2) {
            return Result.failure(IllegalArgumentException("A phrase must contain at least two words: \"$phrase\""))
        }
        if (frequency < 0) return Result.failure(IllegalArgumentException("Invalid frequency: $frequency"))

        return Result.success(PhraseRow(phrase, frequency))
    }
}
