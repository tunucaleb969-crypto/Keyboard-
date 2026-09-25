package com.kwame.aikeyboard

/**
 * Instantly expands common missing-apostrophe contractions ("im" -> "I'm", "dont" ->
 * "don't") without any AI round trip — Phase 12 of the master prompt explicitly wants
 * this handled locally rather than sending every such word to an online AI.
 *
 * Only includes forms that are NOT also valid standalone English words, so a genuine
 * word is never silently rewritten. Deliberately excludes ambiguous cases such as
 * "ill" (sick), "well" (adverb), "wed" (to marry), "were" (past tense of "are"), and
 * "wont" (archaic noun/verb meaning habit/custom — already a recognized word in
 * WordSuggester's own dictionary) — these stay untouched and, if genuinely misspelled,
 * still go through the existing AI-based livecheck correction instead.
 */
object ContractionExpander {
    private val expansions = mapOf(
        "im" to "I'm",
        "ive" to "I've",
        "id" to "I'd",
        "youre" to "you're",
        "youve" to "you've",
        "youll" to "you'll",
        "youd" to "you'd",
        "hes" to "he's",
        "shes" to "she's",
        "theyre" to "they're",
        "theyve" to "they've",
        "theyll" to "they'll",
        "theyd" to "they'd",
        "weve" to "we've",
        "thats" to "that's",
        "whats" to "what's",
        "wheres" to "where's",
        "whos" to "who's",
        "whens" to "when's",
        "hows" to "how's",
        "theres" to "there's",
        "cant" to "can't",
        "dont" to "don't",
        "doesnt" to "doesn't",
        "didnt" to "didn't",
        "isnt" to "isn't",
        "arent" to "aren't",
        "wasnt" to "wasn't",
        "werent" to "weren't",
        "wouldnt" to "wouldn't",
        "couldnt" to "couldn't",
        "shouldnt" to "shouldn't",
        "havent" to "haven't",
        "hasnt" to "hasn't",
        "hadnt" to "hadn't",
        "mustnt" to "mustn't"
    )

    /**
     * Returns the corrected form for [word] if it's an unambiguous missing-apostrophe
     * contraction, preserving the user's own capitalization (so a sentence-starting
     * "Im" becomes "I'm" not "i'm"); returns null if [word] isn't a recognized case.
     */
    fun expand(word: String): String? {
        val expanded = expansions[word.lowercase()] ?: return null
        return if (word.isNotEmpty() && word[0].isUpperCase()) {
            expanded.replaceFirstChar { it.uppercaseChar() }
        } else expanded
    }
}
