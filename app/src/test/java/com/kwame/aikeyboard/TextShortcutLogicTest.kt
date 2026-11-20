package com.kwame.aikeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** JVM-only tests for the pure shortcut rules (no Android classes needed). */
class TextShortcutLogicTest {

    private val gm = TextShortcut("gm", "Good morning", true)
    private val ty = TextShortcut("ty", "Thank you", true)

    // ---- validate ----

    @Test
    fun `valid shortcut passes validation`() {
        assertNull(TextShortcutLogic.validate("gm", "Good morning"))
    }

    @Test
    fun `empty trigger is rejected`() {
        assertNotNull(TextShortcutLogic.validate("   ", "Good morning"))
    }

    @Test
    fun `trigger with a space is rejected`() {
        assertNotNull(TextShortcutLogic.validate("good morning", "Hello"))
    }

    @Test
    fun `trigger over max length is rejected`() {
        val tooLong = "a".repeat(TextShortcutLogic.MAX_TRIGGER_LENGTH + 1)
        assertNotNull(TextShortcutLogic.validate(tooLong, "Hello"))
    }

    @Test
    fun `punctuation only trigger is rejected`() {
        assertNotNull(TextShortcutLogic.validate("..", "Hello"))
        assertNotNull(TextShortcutLogic.validate("-", "Hello"))
    }

    @Test
    fun `trigger with disallowed symbol is rejected`() {
        assertNotNull(TextShortcutLogic.validate("a\$b", "Hello"))
    }

    @Test
    fun `blank expansion is rejected`() {
        assertNotNull(TextShortcutLogic.validate("gm", "   "))
    }

    @Test
    fun `expansion over max length is rejected`() {
        val tooLong = "x".repeat(TextShortcutLogic.MAX_EXPANSION_LENGTH + 1)
        assertNotNull(TextShortcutLogic.validate("gm", tooLong))
    }

    @Test
    fun `expansion identical to trigger is rejected`() {
        assertNotNull(TextShortcutLogic.validate("gm", "GM"))
    }

    // ---- add ----

    @Test
    fun `add appends a new enabled shortcut`() {
        val result = TextShortcutLogic.add(listOf(gm), "ty", "Thank you")
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
        assertEquals("ty", list[1].trigger)
        assertTrue(list[1].enabled)
    }

    @Test
    fun `add trims the trigger`() {
        val list = TextShortcutLogic.add(emptyList(), "  gm  ", "Good morning").getOrThrow()
        assertEquals("gm", list[0].trigger)
    }

    @Test
    fun `add rejects a duplicate trigger ignoring case`() {
        val result = TextShortcutLogic.add(listOf(gm), "GM", "Something else")
        assertTrue(result.isFailure)
    }

    @Test
    fun `add rejects invalid input`() {
        assertTrue(TextShortcutLogic.add(emptyList(), "", "Hello").isFailure)
    }

    @Test
    fun `add is rejected at the max shortcut limit`() {
        val full = (1..TextShortcutLogic.MAX_SHORTCUTS).map { TextShortcut("k$it", "value $it", true) }
        assertTrue(TextShortcutLogic.add(full, "extra", "One more").isFailure)
    }

    // ---- update ----

    @Test
    fun `update changes the expansion and keeps enabled state`() {
        val disabled = gm.copy(enabled = false)
        val list = TextShortcutLogic.update(listOf(disabled), "gm", "gm", "Good morning everyone").getOrThrow()
        assertEquals("Good morning everyone", list[0].expansion)
        assertFalse(list[0].enabled)
    }

    @Test
    fun `update can rename a shortcut`() {
        val list = TextShortcutLogic.update(listOf(gm), "gm", "morning", "Good morning").getOrThrow()
        assertEquals("morning", list[0].trigger)
    }

    @Test
    fun `update cannot rename onto another existing shortcut`() {
        val result = TextShortcutLogic.update(listOf(gm, ty), "gm", "TY", "Good morning")
        assertTrue(result.isFailure)
    }

    @Test
    fun `update of a missing shortcut fails`() {
        assertTrue(TextShortcutLogic.update(listOf(gm), "nope", "nope", "Hello there").isFailure)
    }

    // ---- delete / setEnabled ----

    @Test
    fun `delete removes the shortcut ignoring case`() {
        val list = TextShortcutLogic.delete(listOf(gm, ty), "GM")
        assertEquals(1, list.size)
        assertEquals("ty", list[0].trigger)
    }

    @Test
    fun `delete of an unknown trigger changes nothing`() {
        assertEquals(2, TextShortcutLogic.delete(listOf(gm, ty), "zzz").size)
    }

    @Test
    fun `setEnabled only touches the matching shortcut`() {
        val list = TextShortcutLogic.setEnabled(listOf(gm, ty), "gm", false)
        assertFalse(list[0].enabled)
        assertTrue(list[1].enabled)
    }

    // ---- lookup ----

    @Test
    fun `lookup finds an enabled shortcut ignoring case`() {
        assertEquals("Good morning", TextShortcutLogic.lookup(listOf(gm), "GM")?.expansion)
    }

    @Test
    fun `lookup ignores a disabled shortcut`() {
        assertNull(TextShortcutLogic.lookup(listOf(gm.copy(enabled = false)), "gm"))
    }

    @Test
    fun `lookup returns null for unknown or blank tokens`() {
        assertNull(TextShortcutLogic.lookup(listOf(gm), "hello"))
        assertNull(TextShortcutLogic.lookup(listOf(gm), "  "))
    }

    @Test
    fun `lookup does not match a partial word`() {
        assertNull(TextShortcutLogic.lookup(listOf(gm), "g"))
        assertNull(TextShortcutLogic.lookup(listOf(gm), "gmx"))
    }

    // ---- search ----

    @Test
    fun `blank search returns everything sorted by trigger`() {
        val list = TextShortcutLogic.search(listOf(ty, gm), "")
        assertEquals(listOf("gm", "ty"), list.map { it.trigger })
    }

    @Test
    fun `search matches trigger text`() {
        assertEquals(1, TextShortcutLogic.search(listOf(gm, ty), "gm").size)
    }

    @Test
    fun `search matches expansion text ignoring case`() {
        val list = TextShortcutLogic.search(listOf(gm, ty), "THANK")
        assertEquals(1, list.size)
        assertEquals("ty", list[0].trigger)
    }

    @Test
    fun `search with no match returns empty`() {
        assertTrue(TextShortcutLogic.search(listOf(gm, ty), "zzz").isEmpty())
    }
}
