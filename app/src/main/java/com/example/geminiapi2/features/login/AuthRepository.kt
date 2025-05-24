package com.example.geminiapi2.features.login

import android.util.Log
import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.AuthResponse
import com.example.geminiapi2.data.dto.LoginRequest
import com.example.geminiapi2.data.dto.RegisterRequest
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveToken(it.data.accessToken)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, AuthResponse::class.java).message
                } ?: "Login failed"

                Log.e("A", response.message())
//                Result.failure(Exception("Login failed: ${response.message()}"))
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //register
    suspend fun register(
        username: String,
        email: String,
        password: String,
    ): Result<AuthResponse> {
        return try {
            val response =
                apiService.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveToken(it.data.accessToken)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, AuthResponse::class.java).message
                } ?: "Register failed"

                Log.e("A", response.message())
//                Result.failure(Exception("Login failed: ${response.message()}"))
                Result.failure(Exception(errorMessage))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.deleteToken()
    }
}