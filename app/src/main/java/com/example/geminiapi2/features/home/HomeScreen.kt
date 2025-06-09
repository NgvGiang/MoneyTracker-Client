package com.example.geminiapi2.features.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geminiapi2.features.budget.ui.BudgetScreen
import com.example.geminiapi2.features.dashboard.ui.DashboardScreen
import com.example.geminiapi2.features.navigation.Screen
import com.example.geminiapi2.features.profile.ui.ProfileScreen
import com.example.geminiapi2.features.transaction.ui.TransactionScreen
import com.example.geminiapi2.features.transaction.viewmodel.TransactionViewModel
import com.example.geminiapi2.features.transaction.ui.ExpandableFab


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
fun HomeScreen(
    appNavController: NavController,
    transactionViewModel: TransactionViewModel
) {
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
    }, bottomBar = {
        NavigationBar {
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = navController.currentDestination?.route == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.BottomNav.Dashboard.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { 
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
             },
            exitTransition = { 
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End, 
                    animationSpec = tween(150)
                )
             },
        ) {
            composable(Screen.BottomNav.Dashboard.route) { 
                DashboardScreen(navController = appNavController) 
            }
            composable(Screen.BottomNav.Transaction.route) { 
                TransactionScreen(
                    navController = appNavController,
                    viewModel = transactionViewModel
                ) 
            }
            composable(Screen.BottomNav.Budget.route) { BudgetScreen() }
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