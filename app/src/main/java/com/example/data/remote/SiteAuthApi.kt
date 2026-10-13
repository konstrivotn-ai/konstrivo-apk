package com.example.data.remote

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Same-origin client for the deployed KONSTRIVO website auth API. */
object SiteAuthApi {
    private const val BASE_URL = "https://konstrivo.konstrivo.blitz.cloud/api/v1/auth"
    data class RegistrationResult(val success: Boolean, val message: String)

    fun login(email: String, password: String): Boolean {
        val (status, body) = post("/login", JSONObject().put("email", email).put("password", password))
        return status in 200..299 && !body?.optString("token").isNullOrBlank()
    }

    fun register(fullName: String, email: String, password: String, phone: String, companyName: String): RegistrationResult {
        val payload = JSONObject().put("fullName", fullName).put("email", email).put("password", password)
            .put("phone", phone).put("companyName", companyName).put("role", "artisan")
        val (status, body) = post("/register", payload)
        if (status in 200..299 && !body?.optString("token").isNullOrBlank()) return RegistrationResult(true, "Account created")
        val message = body?.optJSONObject("error")?.optString("message") ?: body?.optString("message")
            ?: if (status == 0) "Cannot connect to KONSTRIVO. Check your internet connection." else "Registration failed (HTTP $status). Please try again."
        return RegistrationResult(false, message)
    }

    private fun post(path: String, payload: JSONObject): Pair<Int, JSONObject?> {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(BASE_URL + path).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 12000
                readTimeout = 12000
                doOutput = true
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            status to runCatching { JSONObject(text) }.getOrNull()
        } catch (_: Exception) {
            0 to null
        } finally {
            connection?.disconnect()
        }
    }
}
