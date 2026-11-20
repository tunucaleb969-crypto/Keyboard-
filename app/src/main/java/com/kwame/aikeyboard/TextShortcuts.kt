package com.kwame.aikeyboard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * A user-defined text shortcut, e.g. trigger "gm" -> expansion "Good morning".
 * Phase 15 of MASTER_PROMPT.md (create / edit / delete / disable / search).
 */
data class TextShortcut(
    val trigger: String,
    val expansion: String,
    val enabled: Boolean = true
)

/**
 * Pure, Android-free shortcut rules. Every function takes a list and returns a new list,
 * so all of it can be unit tested on the JVM (see TextShortcutLogicTest).
 * Triggers are matched case-insensitively and are unique case-insensitively.
 */
object TextShortcutLogic {
    const val MAX_TRIGGER_LENGTH = 20
    const val MAX_EXPANSION_LENGTH = 2000
    const val MAX_SHORTCUTS = 200
    private const val ALLOWED_PUNCTUATION = "'-_@#"

    /** Returns a human-readable error, or null when the trigger/expansion pair is valid. */
    fun validate(trigger: String, expansion: String): String? {
        val t = trigger.trim()
        if (t.isEmpty()) return "Shortcut can't be empty"
        if (t.length > MAX_TRIGGER_LENGTH) return "Shortcut is too long (max $MAX_TRIGGER_LENGTH characters)"
        if (t.any { it.isWhitespace() }) return "Shortcut can't contain spaces"
        if (!t.all { it.isLetterOrDigit() || ALLOWED_PUNCTUATION.indexOf(it) >= 0 }) {
            return "Use only letters, numbers and ' - _ @ #"
        }
        if (!t.any { it.isLetterOrDigit() }) return "Shortcut needs at least one letter or number"
        if (expansion.isBlank()) return "Expansion text can't be empty"
        if (expansion.length > MAX_EXPANSION_LENGTH) {
            return "Expansion is too long (max $MAX_EXPANSION_LENGTH characters)"
        }
        if (t.equals(expansion.trim(), ignoreCase = true)) return "Expansion must be different from the shortcut"
        return null
    }

    fun findIndex(list: List<TextShortcut>, trigger: String): Int {
        val t = trigger.trim()
        return list.indexOfFirst { it.trigger.equals(t, ignoreCase = true) }
    }

    fun add(list: List<TextShortcut>, trigger: String, expansion: String): Result<List<TextShortcut>> {
        validate(trigger, expansion)?.let { return Result.failure(IllegalArgumentException(it)) }
        if (list.size >= MAX_SHORTCUTS) {
            return Result.failure(IllegalArgumentException("You've reached the limit of $MAX_SHORTCUTS shortcuts"))
        }
        val t = trigger.trim()
        if (findIndex(list, t) >= 0) {
            return Result.failure(IllegalArgumentException("A shortcut \"$t\" already exists"))
        }
        return Result.success(list + TextShortcut(t, expansion, true))
    }

    /** Edits an existing shortcut (can rename it). Keeps its enabled state. */
    fun update(
        list: List<TextShortcut>,
        originalTrigger: String,
        newTrigger: String,
        newExpansion: String
    ): Result<List<TextShortcut>> {
        val idx = findIndex(list, originalTrigger)
        if (idx < 0) return Result.failure(IllegalArgumentException("Shortcut not found"))
        validate(newTrigger, newExpansion)?.let { return Result.failure(IllegalArgumentException(it)) }
        val t = newTrigger.trim()
        val clash = findIndex(list, t)
        if (clash >= 0 && clash != idx) {
            return Result.failure(IllegalArgumentException("A shortcut \"$t\" already exists"))
        }
        val updated = list.toMutableList()
        updated[idx] = list[idx].copy(trigger = t, expansion = newExpansion)
        return Result.success(updated)
    }

    fun delete(list: List<TextShortcut>, trigger: String): List<TextShortcut> {
        val t = trigger.trim()
        return list.filterNot { it.trigger.equals(t, ignoreCase = true) }
    }

    fun setEnabled(list: List<TextShortcut>, trigger: String, enabled: Boolean): List<TextShortcut> {
        val t = trigger.trim()
        return list.map { if (it.trigger.equals(t, ignoreCase = true)) it.copy(enabled = enabled) else it }
    }

    /** Finds the ENABLED shortcut whose trigger equals [token] (case-insensitive), or null. */
    fun lookup(list: List<TextShortcut>, token: String): TextShortcut? {
        val t = token.trim()
        if (t.isEmpty()) return null
        return list.firstOrNull { it.enabled && it.trigger.equals(t, ignoreCase = true) }
    }

    /** Matches trigger or expansion text; blank query returns everything. Sorted by trigger. */
    fun search(list: List<TextShortcut>, query: String): List<TextShortcut> {
        val q = query.trim()
        val sorted = list.sortedBy { it.trigger.lowercase() }
        if (q.isEmpty()) return sorted
        return sorted.filter {
            it.trigger.contains(q, ignoreCase = true) || it.expansion.contains(q, ignoreCase = true)
        }
    }
}

/**
 * Persistence layer. Stored in its own private SharedPreferences file so it never touches
 * the existing Prefs object. Text shortcuts may hold personal snippets (addresses etc.),
 * so they are only ever read locally and never sent to the AI.
 */
object TextShortcuts {
    private const val FILE = "ai_keyboard_shortcuts"
    private const val KEY_LIST = "shortcuts_json"
    private const val KEY_ENABLED = "shortcuts_enabled"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    /** Master switch for the whole feature (on by default). */
    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, value).apply()
    }

    fun getAll(context: Context): List<TextShortcut> {
        val raw = prefs(context).getString(KEY_LIST, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            val out = ArrayList<TextShortcut>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                val t = o.optString("t", "")
                val e = o.optString("e", "")
                if (t.isEmpty() || e.isEmpty()) continue
                out.add(TextShortcut(t, e, o.optBoolean("on", true)))
            }
            out
        } catch (ex: Exception) {
            // Corrupt data must never crash the keyboard; treat it as empty.
            emptyList()
        }
    }

    private fun save(context: Context, list: List<TextShortcut>) {
        val arr = JSONArray()
        for (s in list) {
            arr.put(JSONObject().put("t", s.trigger).put("e", s.expansion).put("on", s.enabled))
        }
        prefs(context).edit().putString(KEY_LIST, arr.toString()).apply()
    }

    @Synchronized
    fun add(context: Context, trigger: String, expansion: String): Result<Unit> =
        TextShortcutLogic.add(getAll(context), trigger, expansion).map { save(context, it) }

    @Synchronized
    fun update(context: Context, originalTrigger: String, newTrigger: String, newExpansion: String): Result<Unit> =
        TextShortcutLogic.update(getAll(context), originalTrigger, newTrigger, newExpansion).map { save(context, it) }

    @Synchronized
    fun delete(context: Context, trigger: String) {
        save(context, TextShortcutLogic.delete(getAll(context), trigger))
    }

    @Synchronized
    fun setShortcutEnabled(context: Context, trigger: String, enabled: Boolean) {
        save(context, TextShortcutLogic.setEnabled(getAll(context), trigger, enabled))
    }

    fun search(context: Context, query: String): List<TextShortcut> =
        TextShortcutLogic.search(getAll(context), query)

    /** The text to type in place of [token], or null when the feature is off / no enabled match. */
    fun expansionFor(context: Context, token: String): String? {
        if (!isEnabled(context)) return null
        return TextShortcutLogic.lookup(getAll(context), token)?.expansion
    }

    fun clearAll(context: Context) {
        prefs(context).edit().remove(KEY_LIST).apply()
    }
}
