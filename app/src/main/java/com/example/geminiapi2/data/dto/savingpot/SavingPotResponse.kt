package com.example.geminiapi2.data.dto.savingpot

data class SavingPotResponse(
    val id: Int,
    val potName: String,
    val description: String?,
    val targetAmount: Double,
    val savedAmount: Double,
    val invitationCode: String?,
    val endDate: String?,
    val createdBy: String,
    val createdDate: String,
    val updatedBy: String?,
    val updatedDate: String?,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val userRoles: Map<String, UserRole>?
)

data class UserRole(
    val role: String,
    val joinDate: String
)
