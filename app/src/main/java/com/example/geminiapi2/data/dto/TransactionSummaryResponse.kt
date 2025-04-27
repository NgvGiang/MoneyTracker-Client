package com.example.geminiapi2.data.dto

data class TransactionSummaryResponse(
    val transactions: List<TransactionResponse>,
    val totalIncome: Double,
    val totalExpense: Double,
    val actual: Double // totalIncome - totalExpense
) 