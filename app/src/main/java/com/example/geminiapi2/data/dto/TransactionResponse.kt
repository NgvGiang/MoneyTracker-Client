package com.example.geminiapi2.data.dto

import java.time.LocalDate

data class TransactionResponse(
    val category: String,
    val amount: Double,
    val date: String, // LocalDate được chuyển thành String trong JSON
    val categoryType: String, // "INCOME" hoặc "EXPENSE"
    val userName: String,
    val description: String? = null // Thêm description field, nullable vì có thể API cũ không trả về
)