package com.kwame.aikeyboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GatewayUrlValidatorTest {
    @Test fun acceptsHttpsServiceUrl() {
        assertTrue(GatewayUrlValidator.isValid("https://ai.example.com"))
        assertTrue(GatewayUrlValidator.isValid("https://ai.example.com/api"))
    }

    @Test fun acceptsAndroidEmulatorLoopbackForLocalDevelopment() {
        assertTrue(GatewayUrlValidator.isValid("http://10.0.2.2:8000"))
    }

    @Test fun rejectsInsecureOrMalformedUrls() {
        assertFalse(GatewayUrlValidator.isValid(""))
        assertFalse(GatewayUrlValidator.isValid("https://"))
        assertFalse(GatewayUrlValidator.isValid("http://example.com"))
        assertFalse(GatewayUrlValidator.isValid("https://REPLACE-WITH-RENDER-SERVICE-URL"))
        assertFalse(GatewayUrlValidator.isValid("https://user:pass@example.com"))
        assertFalse(GatewayUrlValidator.isValid("https://example.com/path with spaces"))
    }
}
