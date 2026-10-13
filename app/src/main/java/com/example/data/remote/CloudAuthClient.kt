package com.example.data.remote

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal mobile client for the website's existing Express /api/v1 contract.
 * Uses platform APIs to keep this integration dependency-light and offline-first.
 */
object CloudAuthClient {
    private const val PREFS = "konstrivo_cloud_session"
    private const val ACCESS_TOKEN = "access_token"
    private const val REFRESH_TOKEN = "refresh_token"
    private const val API_BASE = "https://konstrivo.konstrivo.blitz.cloud/api/v1/"

    data class Session(val accessToken: String, val refreshToken: String, val fullName: String, val email: String, val role: String)

    fun login(context: Context, email: String, password: String): Result<Session> =
        requestAuth(context, "auth/login", JSONObject().put("email", email.trim()).put("password", password))

    fun register(
        context: Context,
        fullName: String,
        email: String,
        password: String,
        phone: String,
        companyName: String
    ): Result<Session> = requestAuth(
        context,
        "auth/register",
        JSONObject()
            .put("fullName", fullName.trim())
            .put("email", email.trim())
            .put("password", password)
            .put("phone", phone.trim())
            .put("companyName", companyName.trim())
            .put("role", "artisan")
    )

    fun accessToken(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(ACCESS_TOKEN, null)

    fun logout(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(ACCESS_TOKEN).remove(REFRESH_TOKEN).apply()
    }

    private fun requestAuth(context: Context, path: String, payload: JSONObject): Result<Session> = runCatching {
        val connection = (URL(API_BASE + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 12000
            readTimeout = 12000
            doOutput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }
        try {
            connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            val json = try { JSONObject(response) } catch (_: Exception) { JSONObject() }
            if (status !in 200..299) {
                val message = json.optString("message").ifBlank {
                    when (status) {
                        401 -> "Email or password is incorrect."
                        409 -> "An account with this email already exists."
                        else -> "Server error ($status). Please try again."
                    }
                }
                throw IllegalStateException(message)
            }
            val user = json.optJSONObject("user") ?: JSONObject()
            val access = json.optString("token")
            val refresh = json.optString("refreshToken")
            if (access.isBlank()) throw IllegalStateException("The server did not return a session token.")
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(ACCESS_TOKEN, access)
                .putString(REFRESH_TOKEN, refresh)
                .apply()
            Session(access, refresh, user.optString("fullName"), user.optString("email"), user.optString("role", "artisan"))
        } finally {
            connection.disconnect()
        }
    }
}
