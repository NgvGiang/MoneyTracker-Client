package com.example.geminiapi2.data.dto

class AddTransactionResponse(
    val category: String,
    val date: String,
    val amount: Int,
    val categoryType: String,
    val comment: String
)