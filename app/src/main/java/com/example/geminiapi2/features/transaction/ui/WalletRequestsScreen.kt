package com.example.geminiapi2.features.transaction.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.geminiapi2.data.dto.WalletRequestDTO
import com.example.geminiapi2.features.transaction.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletRequestsScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val walletRequests by viewModel.walletRequests.collectAsState()
    val requestActionResult by viewModel.requestActionResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(requestActionResult) {
        requestActionResult?.let {
            when (it) {
                is TransactionViewModel.RequestActionResult.Success -> {
                    snackbarHostState.showSnackbar(it.message)
                    viewModel.clearRequestActionResult()
                }
                is TransactionViewModel.RequestActionResult.Error -> {
                    snackbarHostState.showSnackbar(it.message)
                    viewModel.clearRequestActionResult()
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.fetchWalletRequests()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Received Requests") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Sent Requests") }
                )
            }
            
            when (selectedTabIndex) {
                0 -> {
                    // Received requests
                    val receivedRequests = walletRequests
                    RequestList(requests = receivedRequests, isSent = false)
                }
                1 -> {
                    // Sent requests - hiện tại chưa triển khai
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tính năng xem các request đã gửi đang được phát triển",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestList(
    requests: List<WalletRequestDTO>,
    isSent: Boolean
) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No ${if (isSent) "sent" else "received"} requests",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(requests) { request ->
                RequestItem(request = request, isSent = isSent)
            }
        }
    }
}

@Composable
fun RequestItem(
    request: WalletRequestDTO,
    isSent: Boolean,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val currentDate = remember { LocalDate.now() }
    val expirationDate = remember(request.expirationDate) {
        try {
            LocalDate.parse(request.expirationDate)
        } catch (e: Exception) {
            null
        }
    }
    
    val isExpired = remember(expirationDate, currentDate) {
        expirationDate?.isBefore(currentDate) ?: false
    }

    val actualStatus = remember(request.status, isExpired) {
        if (isExpired) "EXPIRED" else request.status
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired) Color(0xFFE0E0E0) else Color(0xFFF3F4F5)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Send by: ${request.userName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExpired) Color.Gray else Color(0xFF212121)
                    )
                )
                
                StatusChip(status = actualStatus)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Wallet: ${request.walletName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isExpired) Color.Gray else Color(0xFF616161)
                    )
                )
                
                Text(
                    text = "Expires: ${formatDate(request.expirationDate)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isExpired) Color.Gray else Color(0xFF616161)
                    )
                )
            }
            
            if (!isExpired && request.status == "PENDING") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.respondToInvitation(request.id, "REJECTED") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE25C5C)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Reject")
                    }
                    
                    Button(
                        onClick = { viewModel.respondToInvitation(request.id, "ACCEPTED") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF53D258)
                        )
                    ) {
                        Text("Accept")
                    }
                }
            }

            if (isExpired || request.status == "REJECTED") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { viewModel.removeWalletRequest(request.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Request",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.uppercase()) {
        "PENDING" -> Color(0xFFFFF9C4) to Color(0xFFB76C00)
        "ACCEPTED" -> Color(0xFFD7FFD9) to Color(0xFF0D652D)
        "REJECTED" -> Color(0xFFFFE6E6) to Color(0xFFB00020)
        "EXPIRED" -> Color(0xFFE0E0E0) to Color(0xFF616161)
        else -> Color(0xFFE0E0E0) to Color(0xFF616161)
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        dateString
    }
} 