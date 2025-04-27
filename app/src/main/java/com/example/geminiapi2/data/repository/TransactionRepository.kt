package com.example.geminiapi2.data.repository

import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.TransactionResponse
import com.example.geminiapi2.data.dto.TransactionSummaryResponse
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
        categoryType: String? = null
    ): Result<TransactionSummaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTransactionsByMonthYear(month, year, categoryType)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: TransactionSummaryResponse(
                        transactions = emptyList(),
                        totalIncome = 0.0,
                        totalExpense = 0.0,
                        actual = 0.0
                    ))
                } else {
                    Result.failure(Exception("Failed to fetch transactions: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 