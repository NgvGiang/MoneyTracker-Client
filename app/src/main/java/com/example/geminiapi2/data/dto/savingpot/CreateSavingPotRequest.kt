package com.example.geminiapi2.data.dto.savingpot


data class CreateSavingPotRequest (
    val potName: String,
    val description: String?,
    val targetAmount: Double?
)

