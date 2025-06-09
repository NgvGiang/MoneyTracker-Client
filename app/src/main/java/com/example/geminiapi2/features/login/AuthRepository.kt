package com.example.geminiapi2.features.login

import android.util.Log
import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.ApiErrorParser
import com.example.geminiapi2.data.dto.AuthResponse
import com.example.geminiapi2.data.dto.LoginRequest
import com.example.geminiapi2.data.dto.RegisterRequest
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
                response.body()?.let { authResponse ->
                    if (authResponse.code == "Success" && authResponse.data != null) {
                        tokenManager.saveToken(authResponse.data.accessToken)
                        Result.success(authResponse)
                    } else {
                        Result.failure(Exception(authResponse.message))
                    }
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = ApiErrorParser.parseErrorMessage(errorBody, "Login failed")

                Log.e("AuthRepository", "Login error: $errorMessage")
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
                response.body()?.let { authResponse ->
                    if (authResponse.code == "Success" && authResponse.data != null) {
                        tokenManager.saveToken(authResponse.data.accessToken)
                        Result.success(authResponse)
                    } else {
                        Result.failure(Exception(authResponse.message))
                    }
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = ApiErrorParser.parseErrorMessage(errorBody, "Register failed")

                Log.e("AuthRepository", "Register error: $errorMessage")
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