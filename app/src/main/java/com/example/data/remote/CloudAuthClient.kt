package com.example.data.remote

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Mobile auth client for the website's shared Express /api/v1 account system. */
object CloudAuthClient {
    private const val PREFS = "konstrivo_cloud_session"
    private const val ACCESS_TOKEN = "access_token"
    private const val REFRESH_TOKEN = "refresh_token"
    private const val FULL_NAME = "full_name"
    private const val EMAIL = "email"
    private const val ROLE = "role"
    private const val API_BASE = "https://konstrivo.konstrivo.blitz.cloud/api/v1/"

    data class Session(val accessToken: String, val refreshToken: String, val fullName: String, val email: String, val role: String)

    fun login(context: Context, email: String, password: String): Result<Session> =
        requestAuth(context, "auth/login", JSONObject().put("email", email.trim()).put("password", password))

    fun register(context: Context, fullName: String, email: String, password: String, phone: String, companyName: String): Result<Session> =
        requestAuth(context, "auth/register", JSONObject()
            .put("fullName", fullName.trim()).put("email", email.trim()).put("password", password)
            .put("phone", phone.trim()).put("companyName", companyName.trim()).put("role", "artisan"))

    /** Refresh the saved website session at app startup; transient network errors don't erase it. */
    fun restoreSession(context: Context): Result<Session?> = runCatching {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val refresh = prefs.getString(REFRESH_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return@runCatching null
        val connection = openJsonPost("auth/refresh")
        try {
            connection.outputStream.use { it.write(JSONObject().put("refreshToken", refresh).toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val response = readResponse(connection, status)
            val json = try { JSONObject(response) } catch (_: Exception) { JSONObject() }
            if (status == 401 || status == 403) {
                logout(context)
                return@runCatching null
            }
            if (status !in 200..299) throw IllegalStateException(json.optString("message").ifBlank { "Unable to restore session ($status)." })
            val access = json.optString("token")
            val newRefresh = json.optString("refreshToken").ifBlank { refresh }
            if (access.isBlank()) throw IllegalStateException("The server did not return a refreshed session.")
            prefs.edit().putString(ACCESS_TOKEN, access).putString(REFRESH_TOKEN, newRefresh).apply()
            Session(access, newRefresh, prefs.getString(FULL_NAME, "").orEmpty(),
                prefs.getString(EMAIL, "").orEmpty(), prefs.getString(ROLE, "artisan") ?: "artisan")
        } finally { connection.disconnect() }
    }

    fun requestPasswordReset(email: String): Result<Unit> = runCatching {
        val connection = openJsonPost("auth/forgot")
        try {
            connection.outputStream.use { it.write(JSONObject().put("email", email.trim()).toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val response = readResponse(connection, status)
            if (status !in 200..299) {
                val json = try { JSONObject(response) } catch (_: Exception) { JSONObject() }
                throw IllegalStateException(json.optString("message").ifBlank { "Unable to request password reset. Please try again." })
            }
        } finally { connection.disconnect() }
    }

    fun accessToken(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(ACCESS_TOKEN, null)

    /**
     * Fetch the authoritative account identity from the backend
     * (`GET /users/me`) using the saved access token.
     *
     * Security: the returned role always comes from the SERVER — the role
     * stored in SharedPreferences is never treated as proof of privileges.
     * On 401 the session is refreshed once and the call retried; if the
     * refresh is rejected (expired/revoked) the local session is cleared.
     * Returns null when there is no saved session or it was rejected.
     */
    fun fetchMe(context: Context): Result<Session?> = runCatching {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var access = prefs.getString(ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return@runCatching null
        var result = authorizedGet("users/me", access)
        if (result.first == 401) {
            val refreshed = restoreSession(context)
            if (refreshed.isFailure) return@runCatching null
            access = prefs.getString(ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return@runCatching null
            result = authorizedGet("users/me", access)
        }
        val (status, body) = result
        if (status == 401 || status == 403) {
            logout(context)
            return@runCatching null
        }
        if (status !in 200..299) throw IllegalStateException("Unable to verify the account session ($status).")
        val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
        val user = json.optJSONObject("user") ?: JSONObject()
        val fullName = user.optString("fullName").ifBlank { prefs.getString(FULL_NAME, "").orEmpty() }
        val email = user.optString("email").ifBlank { prefs.getString(EMAIL, "").orEmpty() }
        val role = user.optString("role").ifBlank { prefs.getString(ROLE, "artisan") ?: "artisan" }
        prefs.edit().putString(FULL_NAME, fullName).putString(EMAIL, email).putString(ROLE, role).apply()
        Session(
            prefs.getString(ACCESS_TOKEN, "").orEmpty(),
            prefs.getString(REFRESH_TOKEN, "").orEmpty(),
            fullName, email, role
        )
    }

    private fun authorizedGet(path: String, accessToken: String): Pair<Int, String> {
        val connection = openJsonGet(path)
        try {
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            val status = connection.responseCode
            return status to readResponse(connection, status)
        } finally { connection.disconnect() }
    }

    fun currentSession(context: Context): Session? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val access = prefs.getString(ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return null
        return Session(access, prefs.getString(REFRESH_TOKEN, "").orEmpty(), prefs.getString(FULL_NAME, "").orEmpty(),
            prefs.getString(EMAIL, "").orEmpty(), prefs.getString(ROLE, "artisan") ?: "artisan")
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(ACCESS_TOKEN).remove(REFRESH_TOKEN).remove(FULL_NAME).remove(EMAIL).remove(ROLE).apply()
    }

    private fun requestAuth(context: Context, path: String, payload: JSONObject): Result<Session> = runCatching {
        val connection = openJsonPost(path)
        try {
            connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val response = readResponse(connection, status)
            val json = try { JSONObject(response) } catch (_: Exception) { JSONObject() }
            if (status !in 200..299) {
                val message = json.optString("message").ifBlank {
                    when (status) { 401 -> "Email or password is incorrect."; 409 -> "An account with this email already exists."; else -> "Server error ($status). Please try again." }
                }
                throw IllegalStateException(message)
            }
            val user = json.optJSONObject("user") ?: JSONObject()
            val access = json.optString("token")
            val refresh = json.optString("refreshToken")
            if (access.isBlank()) throw IllegalStateException("The server did not return a session token.")
            val fullName = user.optString("fullName")
            val normalizedEmail = user.optString("email", payload.optString("email"))
            val role = user.optString("role", "artisan")
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(ACCESS_TOKEN, access).putString(REFRESH_TOKEN, refresh)
                .putString(FULL_NAME, fullName).putString(EMAIL, normalizedEmail).putString(ROLE, role).apply()
            Session(access, refresh, fullName, normalizedEmail, role)
        } finally { connection.disconnect() }
    }

    private fun openJsonPost(path: String): HttpURLConnection =
        (URL(API_BASE + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 12000
            readTimeout = 12000
            doOutput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

    private fun openJsonGet(path: String): HttpURLConnection =
        (URL(API_BASE + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12000
            readTimeout = 12000
            setRequestProperty("Accept", "application/json")
        }

    private fun readResponse(connection: HttpURLConnection, status: Int): String {
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        return stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
    }
}
