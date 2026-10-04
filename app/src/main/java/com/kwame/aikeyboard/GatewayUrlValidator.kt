package com.kwame.aikeyboard

import java.net.URI

/** Shared validation for the user-configured AI gateway endpoint. */
object GatewayUrlValidator {
    fun isValid(value: String): Boolean {
        val candidate = value.trim()
        if (candidate.isEmpty() || candidate.any { it.isWhitespace() } || candidate.contains("REPLACE-WITH", ignoreCase = true)) return false
        return try {
            val uri = URI(candidate)
            val scheme = uri.scheme?.lowercase() ?: return false
            val host = uri.host?.lowercase() ?: return false
            val isHttps = scheme == "https"
            val isEmulatorLoopback = scheme == "http" && host == "10.0.2.2"
            (isHttps || isEmulatorLoopback) && uri.userInfo == null && uri.query == null && uri.fragment == null
        } catch (_: Exception) {
            false
        }
    }
}
