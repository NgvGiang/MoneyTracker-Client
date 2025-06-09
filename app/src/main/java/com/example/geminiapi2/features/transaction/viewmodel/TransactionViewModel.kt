package com.example.geminiapi2.features.transaction.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.data.dto.TransactionResponse
import com.example.geminiapi2.data.dto.TransactionSummaryResponse
import com.example.geminiapi2.data.dto.WalletResponse
import com.example.geminiapi2.data.dto.WalletRequestDTO
import com.example.geminiapi2.features.login.TokenManager
import com.example.geminiapi2.features.transaction.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val data: List<TransactionResponse>) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private var lastSelectedWalletId: Int? = null
    private var isInitialized = false
    private var isFetchingWallets = false
    private var isFetchingTransactions = false
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    private val _currentMonth = MutableStateFlow(LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()
    
    private val _currentYear = MutableStateFlow(LocalDate.now().year)
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _categoryType = MutableStateFlow("EXPENSE") // hoặc "INCOME", "EXPENSE"
    val categoryType: StateFlow<String> = _categoryType.asStateFlow()
    private val _wallets = MutableStateFlow<List<WalletResponse>>(emptyList())
    val wallets: StateFlow<List<WalletResponse>> = _wallets.asStateFlow()

    private val _walletId = MutableStateFlow<Int?>(null)
    val walletId: StateFlow<Int?> = _walletId.asStateFlow()

    private val _walletRequests = MutableStateFlow<List<WalletRequestDTO>>(emptyList())
    val walletRequests: StateFlow<List<WalletRequestDTO>> = _walletRequests.asStateFlow()

    private val _joinWalletResult = MutableStateFlow<JoinWalletResult?>(null)
    val joinWalletResult: StateFlow<JoinWalletResult?> = _joinWalletResult.asStateFlow()

    private val _requestActionResult = MutableStateFlow<RequestActionResult?>(null)
    val requestActionResult: StateFlow<RequestActionResult?> = _requestActionResult.asStateFlow()

    sealed class JoinWalletResult {
        object Success : JoinWalletResult()
        data class Error(val message: String) : JoinWalletResult()
    }

    sealed class RequestActionResult {
        data class Success(val message: String) : RequestActionResult()
        data class Error(val message: String) : RequestActionResult()
    }

    fun setCategoryType(type: String) {
        _categoryType.value = type
        fetchTransactions()
    }

    fun setWalletId(id: Int) {
        Log.d("TransactionViewModel", "Setting wallet ID to: $id")
        if (_walletId.value == id) return // Tránh set lại cùng ID
        
        lastSelectedWalletId = id
        _walletId.value = id
        fetchTransactions()
    }

    init {
        Log.d("TransactionViewModel", "Initializing TransactionViewModel")
        // Listen to token changes to reset state when user logs out
        viewModelScope.launch {
            tokenManager.getTokenFlow().collect { token ->
                if (token.isNullOrEmpty()) {
                    resetState()
                } else {
                    fetchWallets()
                }
            }
        }
    }
    
    private fun resetState() {
        Log.d("TransactionViewModel", "Resetting state due to logout")
        _uiState.value = TransactionUiState.Loading
        _wallets.value = emptyList()
        _walletId.value = null
        _walletRequests.value = emptyList()
        _joinWalletResult.value = null
        _requestActionResult.value = null
        _currentMonth.value = LocalDate.now().monthValue
        _currentYear.value = LocalDate.now().year
        _categoryType.value = "EXPENSE"
        lastSelectedWalletId = null
        isInitialized = false
        isFetchingWallets = false
        isFetchingTransactions = false
    }
    
    fun refresh() {
        Log.d("TransactionViewModel", "Refreshing data")
        fetchWallets()
        if (_walletId.value != null) {
            fetchTransactions()
        }
    }

    fun fetchTransactions() {
        if (isFetchingTransactions) return
        
        viewModelScope.launch {
            isFetchingTransactions = true
            val wallet = _walletId.value 
            if (wallet == null) {
                isFetchingTransactions = false
                return@launch
            }
            
            _uiState.value = TransactionUiState.Loading
            Log.d("TransactionViewModel", "Fetching transactions for wallet: $wallet")
            
            try {
                transactionRepository.getTransactionsByMonthYear(
                    month = _currentMonth.value,
                    year = _currentYear.value,
                    walletId = wallet,
                    categoryType = _categoryType.value
                ).onSuccess { data ->
                    _uiState.value = TransactionUiState.Success(data)
                }.onFailure { exception ->
                    _uiState.value = TransactionUiState.Error(exception.message ?: "Unknown error")
                }
            } finally {
                isFetchingTransactions = false
            }
        }
    }
    
    fun fetchWallets() {
        if (isFetchingWallets) return
        
        viewModelScope.launch {
            isFetchingWallets = true
            
            try {
                transactionRepository.getAllWallets()
                    .onSuccess { wallets ->
                        Log.d("TransactionViewModel", "Fetched wallets: ${wallets.map { it.id }}")
                        _wallets.value = wallets
                        
                        val currentWalletId = _walletId.value
                        Log.d("TransactionViewModel", "Current wallet ID: $currentWalletId")
                        
                        // Chỉ set wallet ID khi chưa có hoặc wallet hiện tại không tồn tại
                        when {
                            wallets.isEmpty() -> {
                                _walletId.value = null
                                _uiState.value = TransactionUiState.Error("Create your first wallet RIGHT NOW!")
                            }
                            currentWalletId == null || !wallets.any { it.id == currentWalletId } -> {
                                // Set wallet đầu tiên làm default, nhưng không gọi fetchTransactions ngay
                                val firstWalletId = wallets.first().id
                                Log.d("TransactionViewModel", "Setting first wallet as default: $firstWalletId")
                                _walletId.value = firstWalletId
                                lastSelectedWalletId = firstWalletId
                                
                                // Chỉ fetch transactions nếu đã initialized
                                if (isInitialized) {
                                    fetchTransactions()
                                }
                            }
                            else -> {
                                // Wallet hiện tại vẫn tồn tại, chỉ fetch transactions nếu cần
                                if (isInitialized) {
                                    fetchTransactions()
                                }
                            }
                        }
                        
                        if (!isInitialized) {
                            isInitialized = true
                            // Fetch transactions lần đầu sau khi set wallet
                            if (_walletId.value != null) {
                                fetchTransactions()
                            }
                        }
                    }
                    .onFailure {
                        Log.e("TransactionViewModel", "Error fetching wallets", it)
                        _uiState.value = TransactionUiState.Error("Failed to load wallets")
                    }
            } finally {
                isFetchingWallets = false
            }
        }
    }

    fun updateMonth(month: Int, year: Int) {
        _currentMonth.value = month
        _currentYear.value = year
        fetchTransactions()
    }

    fun createWallet(name: String, balance: Double) {
        viewModelScope.launch {
            transactionRepository.createWallet(name, balance, "VND")
                .onSuccess { 
                    fetchWallets()
                }
                .onFailure {
                    // TODO: Handle error
                }
        }
    }

    fun clearJoinWalletResult() {
        _joinWalletResult.value = null
    }

    fun joinWallet(invitationCode: String) {
        viewModelScope.launch {
            transactionRepository.joinWallet(invitationCode)
                .onSuccess { 
                    _joinWalletResult.value = JoinWalletResult.Success
                    fetchWallets()
                    fetchWalletRequests()
                }
                .onFailure {
                    _joinWalletResult.value = JoinWalletResult.Error(it.message ?: "Failed to join wallet")
                }
        }
    }

    fun fetchWalletRequests() {
        viewModelScope.launch {
            transactionRepository.getAllWalletRequests()
                .onSuccess { requests ->
                    _walletRequests.value = requests
                }
                .onFailure {
                    // Handle error
                }
        }
    }

    fun removeWalletRequest(requestId: Int) {
        viewModelScope.launch {
            transactionRepository.removeWalletRequest(requestId)
                .onSuccess { 
                    _requestActionResult.value = RequestActionResult.Success("Request deleted successfully")
                    fetchWalletRequests()
                }
                .onFailure {
                    _requestActionResult.value = RequestActionResult.Error(it.message ?: "Failed to delete request")
                }
        }
    }

    fun respondToInvitation(requestId: Int, status: String) {
        viewModelScope.launch {
            transactionRepository.respondToInvitation(requestId, status)
                .onSuccess { 
                    _requestActionResult.value = RequestActionResult.Success("Request ${status.lowercase()} successfully")
                    fetchWalletRequests()
                    fetchWallets()
                }
                .onFailure {
                    _requestActionResult.value = RequestActionResult.Error(it.message ?: "Failed to respond to invitation")
                }
        }
    }

    fun clearRequestActionResult() {
        _requestActionResult.value = null
    }
} 