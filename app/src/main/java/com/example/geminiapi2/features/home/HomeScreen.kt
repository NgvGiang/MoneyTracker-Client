package com.example.geminiapi2.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.geminiapi2.features.budget.ui.BudgetScreen
import com.example.geminiapi2.features.dashboard.ui.DashboardScreen
import com.example.geminiapi2.features.navigation.Screen
import com.example.geminiapi2.features.profile.ui.ProfileScreen
import com.example.geminiapi2.features.transaction.ui.TransactionScreen

data class BottomNavItem(
    val title: String, val icon: ImageVector, val route: String
)
//@Composable
//fun SystemBarsColor(isDarkIcons: Boolean) {
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            val insetsController = WindowCompat.getInsetsController(window, view)
//
//            // Đặt màu sắc cho icon hệ thống
//            insetsController.isAppearanceLightStatusBars = isDarkIcons
//            insetsController.isAppearanceLightNavigationBars = isDarkIcons
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(appNavController: NavController) {
    val bottomNavItems = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home, Screen.BottomNav.Dashboard.route),
        BottomNavItem("Transaction", Icons.Default.Wallet, Screen.BottomNav.Transaction.route),
        BottomNavItem("Budget", Icons.Default.PieChart, Screen.BottomNav.Budget.route),
        BottomNavItem("Profile", Icons.Default.Person, Screen.BottomNav.Profile.route)
    )

    val navController = rememberNavController()

    Scaffold(
        topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(Color.LightGray.copy(alpha = 01f))
        )
    }
        , bottomBar = {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            bottomNavItems.forEach { item ->
                NavigationBarItem(icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { appNavController.navigate(Screen.ChatBotAdd.route) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,

        ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.BottomNav.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.BottomNav.Dashboard.route) { DashboardScreen() }
            composable(Screen.BottomNav.Transaction.route) { TransactionScreen(navController = appNavController) }
            composable(Screen.BottomNav.Budget.route) { BudgetScreen() }
//            composable(Screen.BottomNav.Transaction.route) {
//                TransactionScreen(navController = navController)
//            }
            composable(Screen.BottomNav.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        appNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = {
                        appNavController.navigate(Screen.Settings.route)
                    }
                )
            }
        }
    }
}