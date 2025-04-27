package com.example.geminiapi2.features.transaction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()
    
    var showMonthYearPicker by remember { mutableStateOf(false) }
    
    val currentMonthName = remember(currentMonth, currentYear) {
        YearMonth.of(currentYear, currentMonth).month.toString().capitalize() + " " + currentYear
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
//                 Status bar space with gray background
                TopAppBar(
                    title = {
                        Text(
                            text = "Transactions",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        )
                    },
                    actions = {
                        // Calendar Icon Button
                        IconButton(onClick = { showMonthYearPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent

                    )
                )
//            Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
//                            .background(Color.Green.copy(alpha = 0.1f))
//                        )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is TransactionUiState.Loading -> {
                    CircularProgressIndicator(
//                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is TransactionUiState.Error -> {
                    val errorMessage = (uiState as TransactionUiState.Error).message
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
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = "Apr $selectedYear",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                // Year selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedYear",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Row {
                        IconButton(onClick = { selectedYear-- }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Previous Year",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { selectedYear++ }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Next Year",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                
                // Month grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(Month.values()) { month ->
                        val isSelected = month.value == selectedMonth
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(65.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .clickable { selectedMonth = month.value },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = month.toString().take(3),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(selectedYear, selectedMonth) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionContent(
    data: com.example.geminiapi2.data.dto.TransactionSummaryResponse,
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

        // Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Expense",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            formatAmount(data.totalExpense),
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Income",
                            tint = Color(0xFF00BFA5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            formatAmount(data.totalIncome),
                            color = Color(0xFF00BFA5),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Column {
                    Text(
                        formatAmount(data.actual),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (data.actual >= 0) Color(0xFF00BFA5) else Color.Red
                    )
                }
            }
        }

        // Transactions List
        if (data.transactions.isEmpty()) {
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
                modifier = Modifier.padding(top = 4.dp)
            ) {
                var currentDate: String? = null
                
                items(data.transactions) { transaction ->
                    val transactionDate = transaction.date.split("T")[0] // Xử lý format ISO
                    
                    if (currentDate != transactionDate) {
                        currentDate = transactionDate
                        // Date Header
                        Text(
                            text = formatDateFromString(transactionDate),
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Transaction Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Category Icon
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (transaction.catName.lowercase()) {
                                        "entertainment" -> Icons.Default.Celebration
                                        "food" -> Icons.Default.Restaurant
                                        "transport" -> Icons.Default.DirectionsCar
                                        "shopping" -> Icons.Default.ShoppingCart
                                        "health" -> Icons.Default.HealthAndSafety
                                        "education" -> Icons.Default.School
                                        "salary" -> Icons.Default.AttachMoney
                                        else -> Icons.Default.Category
                                    },
                                    contentDescription = transaction.catName,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = transaction.catName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Amount with appropriate color based on category type
                        val amountColor = if (transaction.categoryType.equals("INCOME", ignoreCase = true)) {
                            Color(0xFF00BFA5) // Green for income
                        } else {
                            Color.Red // Red for expense
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