package com.example.geminiapi2.data.dto

data class WalletResponse (
    val id: Int,
    val walletName: String,
    val currentBalance: Double,
    val invitationCode: String,
    val currency: String,
    val createdBy: String,
    val createdDate: String,
    val updatedBy: String,
    val updatedDate: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val userRoles: Map<String, UserRole>
)
data class UserRole(
    val role: String,
    val joinDate: String
)

//wallet.userRoles.forEach { (username, userRole) ->
//    Text("$username: ${userRole.role} (tham gia: ${userRole.joinDate})")
//}