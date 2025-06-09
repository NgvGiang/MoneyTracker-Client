package com.example.geminiapi2.features.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.geminiapi2.data.dto.WalletResponse
import com.example.geminiapi2.features.wallet.viewmodel.WalletDetailUiState
import com.example.geminiapi2.features.wallet.viewmodel.WalletDetailViewModel
import com.example.geminiapi2.features.navigation.Screen
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(
    navController: NavController,
    viewModel: WalletDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Wallet Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = uiState) {
                is WalletDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WalletDetailUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WalletDetailUiState.Success -> {
                    WalletDetailsContent(wallet = state.wallet, navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun WalletDetailsContent(
    wallet: WalletResponse,
    navController: NavController,
    viewModel: WalletDetailViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Thông tin cơ bản của ví
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2F51FF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = wallet.walletName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    
                    Text(
                        text = formatAmount(wallet.currentBalance),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
        
        item {
            // Thông tin chi tiết
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Wallet Information",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )
                    )
                    
                    DetailRow(
                        label = "Currency",
                        value = wallet.currency,
                        labelColor = Color(0xFF616161),
                        valueColor = Color(0xFF212121)
                    )
                    
                    DetailRow(
                        label = "Created By",
                        value = wallet.createdBy,
                        labelColor = Color(0xFF616161),
                        valueColor = Color(0xFF212121)
                    )
                    
                    DetailRow(
                        label = "Created Date",
                        value = formatIsoDate(wallet.createdDate),
                        labelColor = Color(0xFF616161),
                        valueColor = Color(0xFF212121)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailRow(
                            label = "Invite Code",
                            value = if (wallet.invitationCode.isNullOrEmpty()) "-" else wallet.invitationCode,
                            labelColor = Color(0xFF616161),
                            valueColor = if (wallet.invitationCode.isNullOrEmpty()) Color(0xFF616161) else Color(0xFF2F51FF),
                            valueWeight = if (wallet.invitationCode.isNullOrEmpty()) FontWeight.Normal else FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (wallet.invitationCode.isNullOrEmpty()) {
                            Button(
                                onClick = { viewModel.generateInvitationCode() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2F51FF)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "Generate Code",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Manage Categories Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Category Management",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )
                    )
                    
                    Button(
                        onClick = { 
                            navController.navigate(Screen.ManageCategories.createRoute(wallet.id))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2F51FF)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Manage Categories",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
        
        item {
            // Phần Members
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Members",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
        
        wallet.userRoles?.let { roles ->
            items(roles.toList()) { (username, roleInfo) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3F4F5)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar placeholder
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF2F51FF).copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = username.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF2F51FF),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    text = username,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF212121)
                                    )
                                )
                                Text(
                                    text = when (roleInfo.role) {
                                        "ADMIN" -> "Owner"
                                        "MEMBER" -> "Member"
                                        else -> roleInfo.role
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF616161)
                                    )
                                )
                            }
                        }
                        
                        Text(
                            text = if (roleInfo.role == "ADMIN") 
                                "Created ${formatIsoDate(roleInfo.joinDate)}"
                            else 
                                "Joined ${formatIsoDate(roleInfo.joinDate)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF616161)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    labelColor: Color = Color.Black,
    valueColor: Color = Color.Black,
    valueWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = labelColor
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = valueColor,
                fontWeight = valueWeight ?: FontWeight.Normal
            )
        )
    }
}

private fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount).replace("₫", "đ")
}

private fun formatIsoDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "N/A"
    return try {
        // Try parsing OffsetDateTime first (includes timezone)
        val offsetDateTime = OffsetDateTime.parse(dateString)
        offsetDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH))
    } catch (e: Exception) {
        // Fallback for simple date format (like joinDate)
        try {
            val localDate = java.time.LocalDate.parse(dateString)
            localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
        } catch (e2: Exception) {
            dateString // Return original string if all parsing fails
        }
    }
} 