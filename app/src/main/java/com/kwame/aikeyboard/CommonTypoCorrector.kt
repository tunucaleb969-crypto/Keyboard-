package com.kwame.aikeyboard

/**
 * Small, conservative offline correction table for frequent, unambiguous typos.
 * Unknown words return null and continue through the existing correction pipeline.
 * Keep this list evidence-based; never apply a fuzzy guess to every unfamiliar word.
 */
object CommonTypoCorrector {
    private val corrections = mapOf(
        "pqste" to "paste",
        "teh" to "the",
        "adn" to "and",
        "thier" to "their",
        "recieve" to "receive",
        "becuase" to "because",
        "seperate" to "separate",
        "definately" to "definitely",
        "occured" to "occurred",
        "begining" to "beginning"
    )

    fun correct(word: String): String? {
        val corrected = corrections[word.lowercase()] ?: return null
        return when {
            word.all { it.isUpperCase() } -> corrected.uppercase()
            word.firstOrNull()?.isUpperCase() == true -> corrected.replaceFirstChar { it.uppercaseChar() }
            else -> corrected
        }
    }
}
