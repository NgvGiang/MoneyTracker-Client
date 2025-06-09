package com.example.geminiapi2.data.dto

import com.google.gson.Gson



// Error response chung cho tất cả API
data class ErrorResponse(
    val code: String,
    val message: String,
    val data: Any? = null
)

// Response cho login success
data class AuthResponse(
    val code: String,
    val message: String,
    val data: AuthData?
)

data class AuthData(
    val accessToken: String
)

// Utility để parse error response
object ApiErrorParser {
    fun parseErrorMessage(errorBody: String?, defaultMessage: String = "Unknown error"): String {
        return errorBody?.let {
            try {
                Gson().fromJson(it, ErrorResponse::class.java).message
            } catch (e: Exception) {
                defaultMessage
            }
        } ?: defaultMessage
    }
}