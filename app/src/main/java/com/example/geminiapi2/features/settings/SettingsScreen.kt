package com.example.geminiapi2.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.geminiapi2.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    when (uiState) {
        is SettingsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is SettingsUiState.LoggedOut -> {
            LaunchedEffect(key1 = uiState) {
                onNavigateToLogin()
            }
        }
        is SettingsUiState.Error -> {
            val errorState = uiState as SettingsUiState.Error
            LaunchedEffect(key1 = errorState) {
                snackbarHostState.showSnackbar(errorState.message)
            }
            SettingsContent(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onLogout = { viewModel.logout() }
            )
        }
        else -> {
            SettingsContent(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onLogout = { viewModel.logout() }
            )
        }
    }
}

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEEEEEE))
        ) {
            if (uiState is SettingsUiState.Success) {
                // User Profile
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.userName.firstOrNull()?.toString() ?: "U",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF424242)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = uiState.userName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = uiState.userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings Items
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        SettingsItem(
                            icon = R.drawable.category_icon,
                            title = "Manage categories",
                            onClick = { /* TODO: Navigate to categories management */ }
                        )
                        Divider()
                        SettingsItem(
                            icon = R.drawable.export_to_pdf_icon,
                            title = "Export to PDF",
                            onClick = { /* TODO: Export functionality */ }
                        )
                        Divider()
                        SettingsItem(
                            icon = R.drawable.currency_icon,
                            title = "Choose currency",
                            onClick = { /* TODO: Currency settings */ }
                        )
                        Divider()
                        SettingsItem(
                            icon = R.drawable.language_icon,
                            title = "Choose language",
                            onClick = { /* TODO: Language settings */ }
                        )
                        Divider()
                        SettingsItem(
                            icon = R.drawable.faq_icon,
                            title = "Frequently asked questions",
                            onClick = { /* TODO: FAQ screen */ }
                        )
                        Divider()
                        SettingsItem(
                            icon = R.drawable.logout_icon,
                            title = "Logout",
                            onClick = onLogout
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color(0xFF616161),
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            color = Color(0xFF212121)
        )
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFFBDBDBD)
        )
    }
} 