package com.example.geminiapi2.data.dto

import java.time.LocalDate

data class TransactionResponse(
    val catName: String,
    val amount: Double,
    val date: String, // LocalDate được chuyển thành String trong JSON
    val categoryType: String // "INCOME" hoặc "EXPENSE"
)