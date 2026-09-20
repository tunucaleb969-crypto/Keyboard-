package com.kwame.aikeyboard

/**
 * Bounded cache so identical AI requests (same task + same source text) don't
 * re-hit the API. Two separate caches since run() returns a String and
 * runMulti() returns a List<String>.
 */
object AiResponseCache {
    private const val MAX_ENTRIES = 30

    private val singleCache = object : LinkedHashMap<String, String>(MAX_ENTRIES, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
            return size > MAX_ENTRIES
        }
    }

    private val multiCache = object : LinkedHashMap<String, List<String>>(MAX_ENTRIES, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<String>>?): Boolean {
            return size > MAX_ENTRIES
        }
    }

    private fun key(task: String, text: String) = "$task::$text"

    @Synchronized
    fun getSingle(task: String, text: String): String? = singleCache[key(task, text)]

    @Synchronized
    fun putSingle(task: String, text: String, result: String) {
        singleCache[key(task, text)] = result
    }

    @Synchronized
    fun getMulti(task: String, text: String): List<String>? = multiCache[key(task, text)]

    @Synchronized
    fun putMulti(task: String, text: String, result: List<String>) {
        multiCache[key(task, text)] = result
    }
}
