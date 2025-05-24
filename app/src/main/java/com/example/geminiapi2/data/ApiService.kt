package com.example.geminiapi2.data

import com.example.geminiapi2.data.dto.AddTransactionRequest
import com.example.geminiapi2.data.dto.AddTransactionResponse
import com.example.geminiapi2.data.dto.AuthResponse
import com.example.geminiapi2.data.dto.LoginRequest
import com.example.geminiapi2.data.dto.DeviceTokenRequest
import com.example.geminiapi2.data.dto.DeviceTokenResponse
import com.example.geminiapi2.data.dto.RegisterRequest
import com.example.geminiapi2.data.dto.TransactionResponse
import com.example.geminiapi2.data.dto.WalletResponse
import com.example.geminiapi2.data.dto.CreateWalletRequest
import com.example.geminiapi2.data.dto.JoinWalletResponse
import com.example.geminiapi2.data.dto.WalletRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.DELETE

interface ApiService {
    @POST("apiservice/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
    
    @POST("apiservice/api/auth/register")
    suspend fun register(@Body loginRequest: RegisterRequest):Response<AuthResponse>
    
    @POST("chatbot/chat/addTransaction")
    suspend fun addTransaction(@Body request: AddTransactionRequest): Response<AddTransactionResponse>

    @POST("notification/pushDeviceToken")
    suspend fun pushDeviceToken(@Body request: DeviceTokenRequest): Response<DeviceTokenResponse>
    
    @GET("apiservice/api/transactions/get_all_by_wallet_id")
    suspend fun getTransactionsByMonthYear(
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("walletId") walletId: Int,
        @Query("cType") categoryType: String
    ): Response<List<TransactionResponse>>

    @GET("apiservice/api/wallet/get_all")
    suspend fun getAllWallets(): Response<List<WalletResponse>>

    @GET("apiservice/api/wallet/get_by_id")
    suspend fun getWalletById(@Query("id") walletId: Int): Response<WalletResponse>
    
    @GET("apiservice/api/wallet/get_invitation_code/{walletId}")
    suspend fun getInvitationCode(@Path("walletId") walletId: Int): Response<WalletResponse>

    @POST("apiservice/api/wallet/create")
    suspend fun createWallet(@Body request: CreateWalletRequest): Response<WalletResponse>

    @POST("apiservice/api/wallet/create_request/{invitationCode}")
    suspend fun joinWallet(@Path("invitationCode") invitationCode: String): Response<JoinWalletResponse>

    @GET("apiservice/api/wallet/get_all_request")
    suspend fun getAllWalletRequests(): Response<List<WalletRequestDTO>>

    @DELETE("apiservice/api/wallet/remove_request/{requestId}")
    suspend fun removeWalletRequest(@Path("requestId") requestId: Int): Response<String>

    @POST("apiservice/api/wallet/response_invitation")
    suspend fun respondToInvitation(
        @Query("requestId") requestId: Int,
        @Query("status") status: String
    ): Response<WalletResponse>
}