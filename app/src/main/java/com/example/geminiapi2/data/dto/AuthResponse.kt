package com.example.geminiapi2.data.dto

data class AuthResponse(
    val message: String, // Chuỗi thông báo chính
    val data: AuthData   // Dữ liệu chi tiết (nested object)
)

data class AuthData(
    val accessToken: String, // Token JWT
    val message: String      // Thông báo chi tiết
)