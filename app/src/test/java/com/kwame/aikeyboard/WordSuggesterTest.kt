package com.kwame.aikeyboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordSuggesterTest {
    @Test fun blankPrefixReturnsNoSuggestions() {
        assertTrue(WordSuggester.suggest("").isEmpty())
        assertTrue(WordSuggester.suggest("   ").isEmpty())
    }

    @Test fun suggestionsRespectLimitAndPrefix() {
        val results = WordSuggester.suggest("pas", 3)
        assertTrue(results.size <= 3)
        assertTrue(results.all { it.startsWith("pas", ignoreCase = true) })
    }

    @Test fun suggestionsAreCaseInsensitive() {
        assertTrue(WordSuggester.suggest("pas").map { it.lowercase() } ==
            WordSuggester.suggest("PAS").map { it.lowercase() })
    }

    @Test fun nonPositiveLimitReturnsNoSuggestions() {
        assertTrue(WordSuggester.suggest("pas", 0).isEmpty())
        assertTrue(WordSuggester.suggest("pas", -1).isEmpty())
    }

    @Test fun knownWordLookupIsCaseInsensitive() {
        assertTrue(WordSuggester.isKnownWord("Paste"))
        assertTrue(WordSuggester.isKnownWord("PASTE"))
        assertFalse(WordSuggester.isKnownWord("notawordxyz"))
    }
}
