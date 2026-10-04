package com.kwame.aikeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CommonTypoCorrectorTest {
    @Test fun fixesRequestedAdjacentKeyTypoOffline() {
        assertEquals("paste", CommonTypoCorrector.correct("pqste"))
    }

    @Test fun preservesInitialCapitalization() {
        assertEquals("Paste", CommonTypoCorrector.correct("Pqste"))
    }

    @Test fun matchesWithoutCaseSensitivity() {
        assertEquals("the", CommonTypoCorrector.correct("TEH"))
    }

    @Test fun unknownWordsAreNeverGuessed() {
        assertNull(CommonTypoCorrector.correct("qwertyunknown"))
        assertNull(CommonTypoCorrector.correct(""))
    }
}
