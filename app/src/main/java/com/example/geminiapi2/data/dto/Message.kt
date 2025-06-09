package com.example.geminiapi2.data.dto

data class Message(
    val text: String,
    val isFromUser: Boolean,
    val transactionInfo: TransactionInfo? = null
)

data class TransactionInfo(
    val category: String,
    val date: String,
    val amount: Int,
    val categoryType: String,
    val description: String
)