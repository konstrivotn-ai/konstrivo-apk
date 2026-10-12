package com.example.domain.security

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * R-001 — Local owner-admin authority gate.
 *
 * Verifies credentials against a PBKDF2-HMAC-SHA256 credential string instead of
 * storing or comparing a plaintext password:
 *
 *   pbkdf2-sha256$<iterations>$<Base64(salt 16B)>$<Base64(hash 32B)>
 *
 * Values are injected at build time via BuildConfig (the secrets gradle plugin
 * reads the local untracked `.env`; `.env.example` only ever contains an INVALID
 * placeholder, so missing configuration fails closed).
 *
 * SECURITY NOTE: this is a LOCAL authority gate only. The shipped credential hash
 * is embedded in the APK and is NOT a secret; this is not server-side
 * authentication.
 */
object AdminAuth {

    private const val FORMAT_ID = "pbkdf2-sha256"
    private const val MIN_ITERATIONS = 50_000
    private const val MAX_ITERATIONS = 10_000_000
    private const val KEY_LENGTH_BITS = 256
    private const val MIN_SALT_BYTES = 8

    data class ParsedCredential(
        val iterations: Int,
        val salt: ByteArray,
        val expectedHash: ByteArray,
    )

    /** Parses a credential string. Returns null on ANY malformation (fail-closed). */
    fun parse(credential: String?): ParsedCredential? {
        if (credential.isNullOrBlank()) return null
        val parts = credential.trim().split('$')
        if (parts.size != 4 || parts[0] != FORMAT_ID) return null
        val iterations = parts[1].toIntOrNull() ?: return null
        if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) return null
        val salt = decodeBase64(parts[2]) ?: return null
        if (salt.size < MIN_SALT_BYTES) return null
        val expectedHash = decodeBase64(parts[3]) ?: return null
        if (expectedHash.size != KEY_LENGTH_BITS / Byte.SIZE_BITS) return null
        return ParsedCredential(iterations, salt, expectedHash)
    }

    fun derive(password: CharArray, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    /**
     * Constant-time credential verification. Returns false on any missing,
     * malformed or unconfigured input (fail-closed).
     */
    fun verify(
        email: String,
        password: String,
        expectedEmail: String?,
        credential: String?,
    ): Boolean {
        if (email.isBlank() || password.isEmpty()) return false
        if (expectedEmail.isNullOrBlank() || credential.isNullOrBlank()) return false
        val emailMatches = MessageDigest.isEqual(
            email.trim().lowercase().toByteArray(Charsets.UTF_8),
            expectedEmail.trim().lowercase().toByteArray(Charsets.UTF_8),
        )
        if (!emailMatches) return false
        val parsed = parse(credential) ?: return false
        val derived = runCatching {
            derive(password.toCharArray(), parsed.salt, parsed.iterations)
        }.getOrNull() ?: return false
        return MessageDigest.isEqual(derived, parsed.expectedHash)
    }

    private fun decodeBase64(value: String): ByteArray? =
        runCatching { Base64.decode(value.trim(), Base64.DEFAULT) }.getOrNull()
}