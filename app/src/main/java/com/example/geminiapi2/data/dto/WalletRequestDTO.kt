package com.example.geminiapi2.data.dto

data class WalletRequestDTO(
    val id: Int,
    val userName: String,
    val walletId: Int,
    val status: String,
    val expirationDate: String,
    val walletName: String
) 