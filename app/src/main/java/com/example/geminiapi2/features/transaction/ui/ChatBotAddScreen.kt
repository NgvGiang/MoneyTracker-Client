package com.example.geminiapi2.features.transaction.ui
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.geminiapi2.GeminiTextViewModel
import com.example.geminiapi2.data.dto.Message
import com.example.geminiapi2.features.transaction.viewmodel.AddTransactionState
import com.example.geminiapi2.features.transaction.viewmodel.ChatBotViewModel
import com.example.geminiapi2.features.transaction.viewmodel.TransactionViewModel
//import androidx.compose.material.icons.Default
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.geminiapi2.data.dto.TransactionInfo
import java.text.NumberFormat
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.json.JSONObject
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatbotAddScreen(
    onNavigateBack: () -> Unit,
    geminiviewModel: GeminiTextViewModel = hiltViewModel(),
    chatbotViewmodel: ChatBotViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel
){
    var messageText by remember { mutableStateOf("") }
//    val uiState by geminiviewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val messages by chatbotViewmodel.messages.collectAsState()
    val sendStatus by chatbotViewmodel.sendStatus.collectAsState()

    val selectedWalletId by transactionViewModel.walletId.collectAsState()
    
    // Thêm snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Thêm state cho error dialog (giữ cho các lỗi khác)
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Add logging
    LaunchedEffect(selectedWalletId) {
        Log.d("ChatBotAddScreen", "Selected Wallet ID: $selectedWalletId")
    }

    // Theo dõi trạng thái gửi tin nhắn và xử lý lỗi
    LaunchedEffect(sendStatus) {
        when (val currentStatus = sendStatus) {
            is AddTransactionState.Success -> {
                Log.d("ChatBotAddScreen", "Transaction created successfully")
                // Khi giao dịch được tạo thành công, cập nhật lại danh sách ví
                transactionViewModel.fetchWallets()
            }
            is AddTransactionState.Error -> {
                Log.e("ChatBotAddScreen", "Error creating transaction: ${currentStatus.message}")
                
                // Parse JSON response để lấy code và message
                try {
                    val jsonObject = JSONObject(currentStatus.message)
                    val code = jsonObject.optString("code", "")
                    val message = jsonObject.optString("message", currentStatus.message)
                    
                    // Kiểm tra nếu code bắt đầu với "exception.message."
                    if (code.startsWith("exception.message.")) {
                        // Hiển thị snackbar với message từ BE
                        launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    } else {
                        // Hiển thị error dialog cho các lỗi khác
                        errorMessage = message
                        showErrorDialog = true
                    }
                } catch (e: Exception) {
                    // Nếu không parse được JSON, fallback về cách xử lý cũ
                    Log.w("ChatBotAddScreen", "Cannot parse error response as JSON, using fallback")
                    when {
                        currentStatus.message.contains("Insufficient balance", ignoreCase = true) ||
                        currentStatus.message.contains("insufficientBalance", ignoreCase = true) -> {
                            // Hiển thị snackbar cho lỗi insufficient balance
                            launch {
                                snackbarHostState.showSnackbar("Số dư không đủ để thực hiện giao dịch này!")
                            }
                        }
                        else -> {
                            // Hiển thị error dialog cho các lỗi khác
                            errorMessage = currentStatus.message
                            showErrorDialog = true
                        }
                    }
                }
            }
            is AddTransactionState.Loading -> {
                Log.d("ChatBotAddScreen", "Creating transaction...")
            }
            is AddTransactionState.Idle -> {
                Log.d("ChatBotAddScreen", "Transaction state idle")
            }
        }
    }

    // Log messages để debug
    LaunchedEffect(messages) {
        Log.d("ChatBotAddScreen", "Messages updated. Count: ${messages.size}")
        messages.forEach { message ->
            Log.d("ChatBotAddScreen", "Message: isFromUser=${message.isFromUser}, text=${message.text}, hasTransaction=${message.transactionInfo != null}")
        }
    }

    // Error Dialog
    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = { 
                showErrorDialog = false
                errorMessage = ""
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .background(Color.LightGray.copy(alpha = 01f))
            )
            TopAppBar(

                title = { Text("Add new transactions") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),

//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimary
////                    containerColor = Color(0xFF343541),
////                    titleContentColor = Color.White
//                ),
                colors = topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) }

            )
        },
//        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {
            // Chat messages area
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(0)
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues( bottom = 3.dp),
                reverseLayout = true

            ) {
                items(messages) { message ->
                    ChatMessageItem(message)
                }
            }
            // Input area
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                OutlinedTextField(
//                    value = messageText,
//                    onValueChange = { messageText = it },
//                    modifier = Modifier.weight(1f),
//                    placeholder = { Text("Add transactions example: Breakfast 20k...") }
//                )
                TextField(
                    value = messageText,
                    onValueChange = { newValue ->
                        try {
                            Log.d("ChatBotAddScreen", "TextField onValueChange: newValue='$newValue', length=${newValue?.length ?: 0}")
                            if ((newValue?.length ?: 0) <= 120) {
                                messageText = newValue ?: ""
                            }
                        } catch (e: Exception) {
                            Log.e("ChatBotAddScreen", "Error in TextField onValueChange: ${e.message}", e)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F0F0),  // Màu nền xám nhạt
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,  // Ẩn đường viền khi focus
                        unfocusedIndicatorColor = Color.Transparent,
                    ),


                    label = null,
                    placeholder = { Text("Add transactions ...") },
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .weight(1f)
                )

                Button(
                    onClick = {
                        try {
                            Log.d("ChatBotAddScreen", "Button clicked. MessageText: '$messageText', length: ${messageText.length}")
                            
                            if (messageText.isNotBlank()) {
                                selectedWalletId?.let { walletId ->
                                    Log.d("ChatBotAddScreen", "Sending message: '$messageText' to walletId: $walletId")
                                    chatbotViewmodel.sendAddTransactionMessage(messageText, walletId)
                                    messageText = ""
                                } ?: run {
                                    Log.e("ChatBotAddScreen", "No wallet selected, cannot send message")
                                }
                            } else {
                                Log.w("ChatBotAddScreen", "Attempted to send blank message")
                            }
                        } catch (e: Exception) {
                            Log.e("ChatBotAddScreen", "Error in button onClick: ${e.message}", e)
                        }
                    },
                    enabled = messageText.isNotBlank() && sendStatus != AddTransactionState.Loading,
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    if (sendStatus == AddTransactionState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    }else{
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    }
                }
            }
            Text(
                text = "AI may make mistakes",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp,10.dp,0.dp,25.dp)

            )
        }
    }
}

@Composable
fun ChatMessageItem(message: Message) {
    // Xử lý data an toàn trước khi render UI
    val safeText = try {
        message.text ?: ""
    } catch (e: Exception) {
        Log.e("ChatBotAddScreen", "Error accessing message text: ${e.message}", e)
        "Error loading message"
    }
    
    val hasTransactionInfo = try {
        message.transactionInfo != null
    } catch (e: Exception) {
        Log.e("ChatBotAddScreen", "Error checking transaction info: ${e.message}", e)
        false
    }
    
    Log.d("ChatBotAddScreen", "Rendering message: isFromUser=${message.isFromUser}, text='$safeText', textLength=${safeText.length}")
        
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (message.isFromUser) {
            // Hiển thị tin nhắn của người dùng
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(2.dp)
                    .widthIn(max = 300.dp)
            ) {
                Text(
                    text = safeText,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
        } else {
            // Hiển thị tin nhắn từ bot
            Column(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .padding(vertical = 4.dp)
            ) {
                if (hasTransactionInfo) {
                    // Xử lý transaction data an toàn trước khi render
                    val transactionData = try {
                        message.transactionInfo
                    } catch (e: Exception) {
                        Log.e("ChatBotAddScreen", "Error accessing transaction info: ${e.message}", e)
                        null
                    }
                    
                    if (transactionData != null) {
                        // Hiển thị thông tin giao dịch trong một card đẹp
                        TransactionCard(transactionData)
                        
                        // Hiển thị comment riêng biệt
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(2.dp)
                        ) {
                            Text(
                                text = safeText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                            )
                        }
                    } else {
                        // Fallback to normal message nếu có lỗi với transaction data
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(2.dp)
                        ) {
                            Text(
                                text = "Error loading transaction info",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                            )
                        }
                    }
                } else {
                    // Nếu không có thông tin giao dịch, hiển thị tin nhắn bình thường
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(2.dp)
                    ) {
                        Text(
                            text = safeText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: TransactionInfo) {
    val categoryTypeColor = when (transaction.categoryType.lowercase()) {
        "expense" -> Color(0xFFE25C5C)
        "income" -> Color(0xFF53D258)
        else -> MaterialTheme.colorScheme.primary
    }
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F4F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header với icon và category
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Hiển thị emoji nếu có trong category
                val categoryText = transaction.category
                val emoji = if (categoryText.contains(" ")) {
                    categoryText.split(" ")[0]
                } else ""
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (emoji.isNotEmpty()) {
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    Text(
                        text = if (emoji.isNotEmpty()) categoryText.substringAfter(" ") else categoryText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                // Category type chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = categoryTypeColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = transaction.categoryType,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = categoryTypeColor
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // Amount
            Text(
                text = formatAmount(transaction.amount),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = categoryTypeColor
                )
            )
            
            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

private fun formatAmount(amount: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount).replace("₫", "đ")
}

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error Icon
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                
                // Title
                Text(
                    text = "Transaction Error",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                
                // Error Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                // Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}
