package com.example.geminiapi2.features.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ChatBotAdd : Screen("chatbot_add")
    object Settings : Screen("settings")
    object WalletDetail : Screen("wallet_detail/{walletId}") {
        fun createRoute(walletId: Int) = "wallet_detail/$walletId"
    }
    object WalletRequests : Screen("wallet_requests")
    sealed class BottomNav(val route: String) {
        object Dashboard : BottomNav("dashboard")
        object Transaction : BottomNav("transaction")
        object Budget : BottomNav("budget")
        object Profile : BottomNav("profile")
    }
}
