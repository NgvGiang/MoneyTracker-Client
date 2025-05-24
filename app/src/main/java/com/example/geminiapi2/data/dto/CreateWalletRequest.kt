package com.example.geminiapi2.data.dto

data class CreateWalletRequest(
    val walletName: String,
    val currentBalance: Double,
    val currency: String
) 