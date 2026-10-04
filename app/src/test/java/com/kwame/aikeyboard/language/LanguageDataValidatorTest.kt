package com.kwame.aikeyboard.language

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for LanguageDataValidator. Pure JVM, no Android framework/emulator needed.
 *
 * These tests run in GitHub Actions as part of `gradle test --no-daemon`.
 */
class LanguageDataValidatorTest {

    // ---- isSkippableLine ----

    @Test
    fun `blank line is skippable`() {
        assertTrue(LanguageDataValidator.isSkippableLine(""))
        assertTrue(LanguageDataValidator.isSkippableLine("   "))
    }

    @Test
    fun `comment line is skippable`() {
        assertTrue(LanguageDataValidator.isSkippableLine("# a comment"))
        assertTrue(LanguageDataValidator.isSkippableLine("   # indented comment"))
    }

    @Test
    fun `normal data line is not skippable`() {
        assertFalse(LanguageDataValidator.isSkippableLine("the\t100\tcommon"))
    }

    // ---- parseWordLine ----

    @Test
    fun `valid word line parses correctly`() {
        val result = LanguageDataValidator.parseWordLine("the\t100000\tcommon\t")
        assertTrue(result.isSuccess)
        val row = result.getOrThrow()
        assertEquals("the", row.word)
        assertEquals(100000, row.frequency)
        assertEquals("common", row.category)
        assertEquals(null, row.region)
    }

    @Test
    fun `word line with region parses correctly`() {
        val result = LanguageDataValidator.parseWordLine("Accra\t4000\tplace\tGH")
        assertTrue(result.isSuccess)
        assertEquals("GH", result.getOrThrow().region)
    }

    @Test
    fun `word line missing columns is rejected`() {
        assertTrue(LanguageDataValidator.parseWordLine("the\t100000").isFailure)
    }

    @Test
    fun `word with digits is rejected`() {
        assertTrue(LanguageDataValidator.parseWordLine("word2\t100\tcommon").isFailure)
    }

    @Test
    fun `word with space is rejected`() {
        assertTrue(LanguageDataValidator.parseWordLine("new york\t100\tplace").isFailure)
    }

    @Test
    fun `word with apostrophe is accepted`() {
        assertTrue(LanguageDataValidator.parseWordLine("don't\t100\tcommon").isSuccess)
    }

    @Test
    fun `negative frequency is rejected`() {
        assertTrue(LanguageDataValidator.parseWordLine("the\t-5\tcommon").isFailure)
    }

    @Test
    fun `non-numeric frequency is rejected`() {
        assertTrue(LanguageDataValidator.parseWordLine("the\tabc\tcommon").isFailure)
    }

    @Test
    fun `unknown category is rejected`() {
        assertTrue(LanguageDataValidator.parseWordLine("the\t100\tnotarealcategory").isFailure)
    }

    @Test
    fun `word exceeding max length is rejected`() {
        val tooLong = "a".repeat(46)
        assertTrue(LanguageDataValidator.parseWordLine("$tooLong\t100\tcommon").isFailure)
    }

    // ---- parseCorrectionLine ----

    @Test
    fun `valid correction line parses correctly`() {
        val result = LanguageDataValidator.parseCorrectionLine("teh\tthe\t0.98")
        assertTrue(result.isSuccess)
        val row = result.getOrThrow()
        assertEquals("teh", row.misspelling)
        assertEquals("the", row.correction)
        assertEquals(0.98, row.confidence, 0.0001)
    }

    @Test
    fun `correction line without confidence defaults to 1_0`() {
        val result = LanguageDataValidator.parseCorrectionLine("teh\tthe")
        assertTrue(result.isSuccess)
        assertEquals(1.0, result.getOrThrow().confidence, 0.0001)
    }

    @Test
    fun `identical misspelling and correction is rejected`() {
        assertTrue(LanguageDataValidator.parseCorrectionLine("the\tthe\t1.0").isFailure)
    }

    @Test
    fun `identical misspelling and correction case-insensitive is rejected`() {
        assertTrue(LanguageDataValidator.parseCorrectionLine("The\tthe\t1.0").isFailure)
    }

    @Test
    fun `confidence above 1 is rejected`() {
        assertTrue(LanguageDataValidator.parseCorrectionLine("teh\tthe\t1.5").isFailure)
    }

    @Test
    fun `confidence below 0 is rejected`() {
        assertTrue(LanguageDataValidator.parseCorrectionLine("teh\tthe\t-0.1").isFailure)
    }

    // ---- parseBigramLine ----

    @Test
    fun `valid bigram line parses correctly`() {
        val result = LanguageDataValidator.parseBigramLine("thank\tyou\t100")
        assertTrue(result.isSuccess)
        val row = result.getOrThrow()
        assertEquals("thank", row.previousWord)
        assertEquals("you", row.nextWord)
        assertEquals(100, row.frequency)
    }

    @Test
    fun `bigram line without frequency defaults to 1`() {
        val result = LanguageDataValidator.parseBigramLine("thank\tyou")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().frequency)
    }

    // ---- parseEmojiLine ----

    @Test
    fun `valid emoji line parses correctly`() {
        val result = LanguageDataValidator.parseEmojiLine("love\t\u2764\ufe0f")
        assertTrue(result.isSuccess)
        assertEquals("love", result.getOrThrow().word)
    }

    @Test
    fun `emoji line with overly long emoji field is rejected`() {
        assertTrue(LanguageDataValidator.parseEmojiLine("word\tnotanemojiatall").isFailure)
    }

    // ---- parsePhraseLine ----

    @Test
    fun `valid phrase line parses correctly`() {
        val result = LanguageDataValidator.parsePhraseLine("thank you so much\t80")
        assertTrue(result.isSuccess)
        assertEquals("thank you so much", result.getOrThrow().phrase)
        assertEquals(80, result.getOrThrow().frequency)
    }

    @Test
    fun `single-word phrase is rejected`() {
        assertTrue(LanguageDataValidator.parsePhraseLine("hello\t80").isFailure)
    }

    @Test
    fun `phrase without frequency defaults to 0`() {
        val result = LanguageDataValidator.parsePhraseLine("let me know")
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().frequency)
    }
}
