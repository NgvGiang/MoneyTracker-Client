package com.example.geminiapi2.features.transaction.repository

import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.TransactionResponse
import com.example.geminiapi2.data.dto.TransactionSummaryResponse
import com.example.geminiapi2.data.dto.WalletResponse
import com.example.geminiapi2.data.dto.CreateWalletRequest
import com.example.geminiapi2.data.dto.JoinWalletResponse
import com.example.geminiapi2.data.dto.WalletRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getTransactionsByMonthYear(
        month: Int,
        year: Int,
        walletId : Int,
        categoryType: String
    ): Result<List<TransactionResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTransactionsByMonthYear(month, year,walletId, categoryType)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch transactions: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
//    suspend fun getTransactionsByMonthYear(
//        month: Int,
//        year: Int,
//        walletId: Int,
//        categoryType: String
//    ): Result<List<TransactionResponse>> = withContext(Dispatchers.IO) {
//        runCatching {
//            val response = apiService.getTransactionsByMonthYear(month, year, walletId, categoryType)
//            if (response.isSuccessful) {
//                response.body() ?: emptyList()
//            } else {
//                throw Exception("Failed to fetch transactions: ${response.code()} ${response.message()}")
//            }
//        }
//    }
    suspend fun getAllWallets(): Result<List<WalletResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllWallets()
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch wallets: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getWalletById(walletId: Int): Result<WalletResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWalletById(walletId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch wallet details: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getInvitationCode(walletId: Int): Result<WalletResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getInvitationCode(walletId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get invitation code: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createWallet(walletName: String, currentBalance: Double, currency: String): Result<WalletResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateWalletRequest(walletName, currentBalance, currency)
                val response = apiService.createWallet(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to create wallet: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun joinWallet(invitationCode: String): Result<JoinWalletResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.joinWallet(invitationCode)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to join wallet: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getAllWalletRequests(): Result<List<WalletRequestDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllWalletRequests()
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch wallet requests: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun removeWalletRequest(requestId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.removeWalletRequest(requestId)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: "Request deleted successfully")
                } else {
                    Result.failure(Exception("Failed to delete request: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun respondToInvitation(requestId: Int, status: String): Result<WalletResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.respondToInvitation(requestId, status)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to respond to invitation: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 