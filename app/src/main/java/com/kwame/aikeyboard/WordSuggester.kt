package com.kwame.aikeyboard

object WordSuggester {
    private val words = listOf(
        "the","be","to","of","and","a","in","that","have","i","it","for","not","on","with","he",
        "as","you","do","at","this","but","his","by","from","they","we","say","her","she","or",
        "an","will","my","one","all","would","there","their","what","so","up","out","if","about",
        "who","get","which","go","me","when","make","can","like","time","no","just","him","know",
        "take","people","into","year","your","good","some","could","them","see","other","than",
        "then","now","look","only","come","its","over","think","also","back","after","use","two",
        "how","our","work","first","well","way","even","new","want","because","any","these","give",
        "day","most","us","is","am","are","was","were","been","being","has","had","does","did",
        "please","thanks","thank","hello","hi","hey","meeting","today","tomorrow","project","email",
        "call","later","soon","great","sorry","need","help","let","know","sure","okay","yes","no",
        "love","really","very","much","happy","best","regards","team","update","schedule","free",
        "busy","available","tonight","morning","afternoon","evening","week","month","asap","review"
    )

    fun suggest(prefix: String, limit: Int = 3): List<String> {
        if (prefix.isBlank()) return emptyList()
        val lower = prefix.lowercase()
        return words.filter { it.startsWith(lower) && it != lower }.take(limit)
    }
}
