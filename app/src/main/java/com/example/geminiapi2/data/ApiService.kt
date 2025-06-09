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
import com.example.geminiapi2.data.dto.ManualAddTransactionRequest
import com.example.geminiapi2.data.dto.CatDTO
import com.example.geminiapi2.data.dto.CreateCategoryRequest
import com.example.geminiapi2.data.dto.UpdateCategoryRequest
import com.example.geminiapi2.data.dto.SummaryRequestDTO
import com.example.geminiapi2.data.dto.SummaryResponseDTO
import com.example.geminiapi2.data.dto.MacroResponse
import com.example.geminiapi2.data.dto.MacroRequest
import com.example.geminiapi2.data.dto.savingpot.CreateSavingPotRequest
import com.example.geminiapi2.data.dto.savingpot.SavingPotResponse
import okhttp3.ResponseBody
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

    @POST("apiservice/api/transactions/create")
    suspend fun addManualTransaction(@Body request: ManualAddTransactionRequest): Response<TransactionResponse>

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
    suspend fun removeWalletRequest(@Path("requestId") requestId: Int): Response<ResponseBody>

    @POST("apiservice/api/wallet/response_invitation")
    suspend fun respondToInvitation(
        @Query("requestId") requestId: Int,
        @Query("status") status: String
    ): Response<WalletResponse>

    @GET("apiservice/api/categories/wallet/{walletId}")
    suspend fun getUserCategories(@Path("walletId") walletId: Int): Response<List<CatDTO>>

    @POST("apiservice/api/categories/create")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Response<CatDTO>

    @POST("apiservice/api/categories/update")
    suspend fun updateCategory(@Body request: UpdateCategoryRequest): Response<CatDTO>

    @DELETE("apiservice/api/categories/delete/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<ResponseBody>

    @POST("apiservice/api/transactions/get_summary")
    suspend fun getSummary(@Body request: SummaryRequestDTO): Response<SummaryResponseDTO>

    @GET("apiservice/api/macro/get")
    suspend fun getMacro(): Response<MacroResponse>

    @POST("apiservice/api/macro/create")
    suspend fun createMacro(@Body request: MacroRequest): Response<MacroResponse>

    // Saving Pots zone
    @POST("/apiservice/api/saving-pots/create")
    suspend fun createSavingPot(@Body request: CreateSavingPotRequest): Response<SavingPotResponse>

    @GET("/apiservice/api/saving-pots/all")
    suspend fun getAllSavingPots(): Response<List<SavingPotResponse>>

}