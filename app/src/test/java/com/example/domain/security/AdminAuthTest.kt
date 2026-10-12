package com.example.domain.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * R-001 tests for [AdminAuth].
 *
 * The credential below embeds a deterministic TEST FIXTURE
 * (fixture password "KstrvFixVec-2026-R001", salt "0123456789abcdef",
 * 150000 iterations of PBKDF2-HMAC-SHA256). It is NOT a real secret.
 *
 * The exact same vector is asserted in web/tests/admin-auth.test.ts so that any
 * divergence between the Android and Web implementations breaks CI.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AdminAuthTest {

    private val testEmail = "owner@example.com"
    private val testPassword = "KstrvFixVec-2026-R001"
    private val testCredential =
        "pbkdf2-sha256\$150000\$MDEyMzQ1Njc4OWFiY2RlZg==\$JFL+WmKUf5vtIfaaXIoqnFtIMBTThToSSKKkQJ0PrYE="

    @Test
    fun `parse accepts valid credential`() {
        val parsed = AdminAuth.parse(testCredential)
        assertNotNull(parsed)
        assertEquals(150_000, parsed!!.iterations)
        assertEquals(16, parsed.salt.size)
        assertEquals(32, parsed.expectedHash.size)
    }

    @Test
    fun `parse fails closed on unconfigured placeholder and blank input`() {
        assertNull(AdminAuth.parse("UNCONFIGURED_CHANGE_ME"))
        assertNull(AdminAuth.parse(null))
        assertNull(AdminAuth.parse(""))
        assertNull(AdminAuth.parse("   "))
    }

    @Test
    fun `parse fails closed on malformed credentials`() {
        assertNull(AdminAuth.parse("sha256\$150000\$MDEyMzQ1Njc4OWFiY2RlZg==\$JFL+WmKUf5vtIfaaXIoqnFtIMBTThToSSKKkQJ0PrYE="))
        assertNull(AdminAuth.parse("pbkdf2-sha256\$abc\$MDEyMzQ1Njc4OWFiY2RlZg==\$JFL+WmKUf5vtIfaaXIoqnFtIMBTThToSSKKkQJ0PrYE="))
        assertNull(AdminAuth.parse("pbkdf2-sha256\$1000\$MDEyMzQ1Njc4OWFiY2RlZg==\$JFL+WmKUf5vtIfaaXIoqnFtIMBTThToSSKKkQJ0PrYE="))
        assertNull(AdminAuth.parse("pbkdf2-sha256\$150000\$!!!not-base64!!!\$JFL+WmKUf5vtIfaaXIoqnFtIMBTThToSSKKkQJ0PrYE="))
        assertNull(AdminAuth.parse("pbkdf2-sha256\$150000\$MDEyMzQ1Njc4OWFiY2RlZg=="))
    }

    @Test
    fun `verify accepts correct credentials`() {
        assertTrue(AdminAuth.verify(testEmail, testPassword, testEmail, testCredential))
    }

    @Test
    fun `verify compares email case-insensitively but rejects other emails`() {
        assertTrue(AdminAuth.verify("Owner@Example.COM", testPassword, testEmail, testCredential))
        assertFalse(AdminAuth.verify(testEmail, testPassword, "other@example.com", testCredential))
    }

    @Test
    fun `verify rejects wrong password`() {
        assertFalse(AdminAuth.verify(testEmail, "wrong-password", testEmail, testCredential))
    }

    @Test
    fun `verify rejects tampered hash with constant-time compare`() {
        val segments = testCredential.split('$')
        val hash = segments[3]
        val flippedFirst = if (hash[0] == 'J') 'I' else 'J'
        val tampered = segments[0] + "\$" + segments[1] + "\$" + segments[2] + "\$" + flippedFirst + hash.substring(1)
        assertFalse(AdminAuth.verify(testEmail, testPassword, testEmail, tampered))
    }

    @Test
    fun `verify fails closed on missing or unconfigured values`() {
        assertFalse(AdminAuth.verify(testEmail, testPassword, null, testCredential))
        assertFalse(AdminAuth.verify(testEmail, testPassword, testEmail, null))
        assertFalse(AdminAuth.verify(testEmail, testPassword, "", testCredential))
        assertFalse(AdminAuth.verify(testEmail, testPassword, testEmail, "UNCONFIGURED_CHANGE_ME"))
        assertFalse(AdminAuth.verify("", testPassword, testEmail, testCredential))
        assertFalse(AdminAuth.verify(testEmail, "", testEmail, testCredential))
    }
}
