package com.example.geminiapi2.features.transaction.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.geminiapi2.data.dto.TransactionResponse
import com.example.geminiapi2.data.dto.WalletResponse
import com.example.geminiapi2.features.navigation.Screen
import com.example.geminiapi2.features.transaction.viewmodel.TransactionUiState
import com.example.geminiapi2.features.transaction.viewmodel.TransactionViewModel
import com.example.geminiapi2.ui.theme.primaryLightFigma
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val selectedWalletId by viewModel.walletId.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()
    val categoryType by viewModel.categoryType.collectAsState()
    val joinWalletResult by viewModel.joinWalletResult.collectAsState()
    
    var showCreateWalletDialog by remember { mutableStateOf(false) }
    var showJoinWalletDialog by remember { mutableStateOf(false) }
    var showMonthYearPicker by remember { mutableStateOf(false) }
    val selectedCategoryType by viewModel.categoryType.collectAsState()
    val currentMonthName = remember(currentMonth, currentYear) {
        YearMonth.of(currentYear, currentMonth).month.toString().capitalize() + " " + currentYear
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show snackbar if join wallet result is available
    LaunchedEffect(joinWalletResult) {
        joinWalletResult?.let {
            when (it) {
                is TransactionViewModel.JoinWalletResult.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Your request has been sent")
                        viewModel.clearJoinWalletResult()
                    }
                }
                is TransactionViewModel.JoinWalletResult.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: ${it.message}")
                        viewModel.clearJoinWalletResult()
                    }
                }
            }
        }
    }

    // Refresh data when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    if (showCreateWalletDialog) {
        CreateWalletDialog(
            onDismiss = { showCreateWalletDialog = false },
            onConfirm = { name, balance -> 
                viewModel.createWallet(name, balance)
            }
        )
    }

    if (showJoinWalletDialog) {
        JoinWalletDialog(
            onDismiss = { showJoinWalletDialog = false },
            onConfirm = { code -> 
                viewModel.joinWallet(code)
            }
        )
    }

    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            initialYear = currentYear,
            initialMonth = currentMonth,
            onDismiss = { showMonthYearPicker = false },
            onConfirm = { year, month ->
                viewModel.updateMonth(month, year)
                showMonthYearPicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = primaryLightFigma
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wallets",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = { navController.navigate(Screen.WalletRequests.route) }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Wallet Requests",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = { showJoinWalletDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.GroupAdd,
                                    contentDescription = "Join Wallet",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = { showMonthYearPicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Calendar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(wallets) { wallet ->
                            WalletCard(
                                wallet = wallet,
                                isSelected = wallet.id == selectedWalletId,
                                onSelect = { viewModel.setWalletId(wallet.id) },
                                navController = navController
                            )
                        }
                        
                        item {
                            Card(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(90.dp)
                                    .clickable { showCreateWalletDialog = true },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF3F4F5)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Wallet",
                                        tint = Color(0xFF2F51FF),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Segment Control cho INCOME/EXPENSE
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F4F5)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (selectedCategoryType == "EXPENSE") 
                                    Color(0xFFE25C5C) 
                                else Color.Transparent,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { viewModel.setCategoryType("EXPENSE") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EXPENSE",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = if (selectedCategoryType == "EXPENSE") 
                                    Color.White 
                                else Color.Gray
                            )
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (selectedCategoryType == "INCOME") 
                                    Color(0xFF53D258)
                                else Color.Transparent,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { viewModel.setCategoryType("INCOME") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "INCOME",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = if (selectedCategoryType == "INCOME") 
                                    Color.White 
                                else Color.Gray
                            )
                        )
                    }
                }
            }

            when (uiState) {
                is TransactionUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TransactionUiState.Error -> {
                    val errorMessage = (uiState as TransactionUiState.Error).message
                    Log.e("TransactionError", errorMessage)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: $errorMessage",

                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchTransactions() }
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is TransactionUiState.Success -> {
                    val data = (uiState as TransactionUiState.Success).data
                    TransactionContent(
                        data = data,
                        currentMonthName = currentMonthName,
                        onMonthSelectorClick = { showMonthYearPicker = true }
                    )
                }
            }
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Year selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { selectedYear-- },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF3F4F5), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Year",
                            tint = Color(0xFF212121)
                        )
                    }
                    
                    Text(
                        text = "$selectedYear",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )
                    )
                    
                    IconButton(
                        onClick = { selectedYear++ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF3F4F5), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Year",
                            tint = Color(0xFF212121)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Month grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Month.values()) { month ->
                        val isSelected = month.value == selectedMonth
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Color(0xFF2F51FF)
                                    else Color(0xFFF3F4F5)
                                )
                                .clickable { selectedMonth = month.value },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = month.toString().take(3),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF212121)
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2F51FF)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2F51FF))
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    
                    Button(
                        onClick = { onConfirm(selectedYear, selectedMonth) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2F51FF)
                        )
                    ) {
                        Text(
                            text = "Select",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionContent(
    data: List<TransactionResponse>,
    currentMonthName: String,
    onMonthSelectorClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Month Selector
        OutlinedButton(
            onClick = onMonthSelectorClick,
            modifier = Modifier.padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Calendar",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentMonthName,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Drop Down",
                modifier = Modifier.size(18.dp)
            )
        }

        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions for this period",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(data) { transaction ->
                    val transactionDate = transaction.date.split("T")[0] // Xử lý format ISO
                    val formattedDate = formatDateFromString(transactionDate) // Giữ format hiện tại
                    
                    // Transaction Item Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3F4F5) // Màu nền xám nhạt
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = transaction.category,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giữa Category và Date
                                    Text(
                                        text = formattedDate, // Hiển thị ngày đã format
                                        style = MaterialTheme.typography.bodySmall.copy( // Style nhỏ hơn, màu xám
                                            color = Color(0xFF707070), // Màu xám từ Figma
                                            fontSize = 11.sp // Cỡ chữ từ Figma
                                        )
                                    )
                                }
                            }
                            
                            // Amount with appropriate color based on category type
                            val amountColor = if (transaction.categoryType.equals("INCOME", ignoreCase = true)) {
                                Color(0xFF53D258)
                            } else {
                                Color(0xFFE25C5C)
                            }
                            
                            Text(
                                text = formatAmount(transaction.amount),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = amountColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDateFromString(dateString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)
    val date = LocalDate.parse(dateString, inputFormatter)
    return date.format(outputFormatter)
}

private fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount).replace("₫", "đ")
}

private fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { 
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
    }
}

@Composable
fun WalletCard(
    wallet: WalletResponse,
    isSelected: Boolean,
    onSelect: () -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(90.dp)
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isSelected) {
                            listOf(Color(0xFF2F51FF), Color(0xFF0E33F3))
                        } else {
                            listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
                        }
                    )
                )
                .padding(16.dp)
        ) {
            // Icon Edit ở góc trên bên phải
            IconButton(
                onClick = { navController.navigate(Screen.WalletDetail.createRoute(wallet.id)) },
                modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(0.dp) // Căn chỉnh và kích thước
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Wallet",
                    tint = if (isSelected) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp) // Kích thước icon nhỏ hơn
                )
            }
            
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wallet.walletName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            letterSpacing = 0.02.em,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false) // Để Text không chiếm hết không gian khi có Icon
                    )
                }
                
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            letterSpacing = 0.02.em,
                            color = (if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
                        )
                    )
                    
                    Text(
                        text = formatAmount(wallet.currentBalance),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            letterSpacing = 0.02.em,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}