package com.kwame.aikeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContractionExpanderTest {
    @Test fun expandsCommonMissingApostrophesLocally() {
        assertEquals("I'm", ContractionExpander.expand("im"))
        assertEquals("don't", ContractionExpander.expand("dont"))
        assertEquals("you're", ContractionExpander.expand("youre"))
        assertEquals("can't", ContractionExpander.expand("cant"))
    }

    @Test fun preservesSentenceInitialCapitalization() {
        assertEquals("I'm", ContractionExpander.expand("Im"))
        assertEquals("Don't", ContractionExpander.expand("Dont"))
    }

    @Test fun leavesAmbiguousStandaloneWordsAlone() {
        assertNull(ContractionExpander.expand("ill"))
        assertNull(ContractionExpander.expand("well"))
        assertNull(ContractionExpander.expand("were"))
        assertNull(ContractionExpander.expand("wont"))
    }

    @Test fun doesNotRewriteUnknownWordsOrPunctuation() {
        assertNull(ContractionExpander.expand("im!"))
        assertNull(ContractionExpander.expand("notacontraction"))
        assertNull(ContractionExpander.expand(""))
    }
}
