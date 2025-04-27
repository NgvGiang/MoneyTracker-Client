package com.example.geminiapi2.data

import com.example.geminiapi2.data.dto.AddTransactionRequest
import com.example.geminiapi2.data.dto.AddTransactionResponse
import com.example.geminiapi2.data.dto.AuthResponse
import com.example.geminiapi2.data.dto.LoginRequest
import com.example.geminiapi2.data.dto.DeviceTokenRequest
import com.example.geminiapi2.data.dto.DeviceTokenResponse
import com.example.geminiapi2.data.dto.TransactionResponse
import com.example.geminiapi2.data.dto.TransactionSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("apiservice/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
    
    @POST("apiservice/api/auth/register")
    suspend fun register(@Body loginRequest: RegisterRequest):Response<AuthResponse>
    
    @POST("chatbot/chat/addTransaction")
    suspend fun addTransaction(@Body request: AddTransactionRequest): Response<AddTransactionResponse>

    @POST("notification/pushDeviceToken")
    suspend fun pushDeviceToken(@Body request: DeviceTokenRequest): Response<DeviceTokenResponse>
    
    @GET("apiservice/api/transactions/by-month-year")
    suspend fun getTransactionsByMonthYear(
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("categoryType") categoryType: String? = null
    ): Response<TransactionSummaryResponse>
}