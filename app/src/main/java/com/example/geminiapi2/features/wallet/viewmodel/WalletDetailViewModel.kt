package com.example.geminiapi2.features.wallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.data.dto.WalletResponse
import com.example.geminiapi2.features.login.TokenManager
import com.example.geminiapi2.features.transaction.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WalletDetailUiState {
    object Loading : WalletDetailUiState()
    data class Success(val wallet: WalletResponse) : WalletDetailUiState()
    data class Error(val message: String) : WalletDetailUiState()
}

@HiltViewModel
class WalletDetailViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<WalletDetailUiState>(WalletDetailUiState.Loading)
    val uiState: StateFlow<WalletDetailUiState> = _uiState.asStateFlow()

    private var _walletId = MutableStateFlow<Int?>(null)
    val walletId: StateFlow<Int?> = _walletId.asStateFlow()

    // Cache wallet data để tránh reload không cần thiết
    private var cachedWallet: WalletResponse? = null
    private var isDataLoaded = false

    init {
        savedStateHandle.get<Int>("walletId")?.let { id ->
            setWalletId(id)
        }
        
        // Listen to token changes to reset state when user logs out
        viewModelScope.launch {
            tokenManager.getTokenFlow().collect { token ->
                if (token.isNullOrEmpty()) {
                    resetState()
                }
            }
        }
    }
    
    private fun resetState() {
        _uiState.value = WalletDetailUiState.Loading
        _walletId.value = null
        cachedWallet = null
        isDataLoaded = false
    }

    fun setWalletId(id: Int) {
        _walletId.value = id
        fetchWalletDetails(id)
    }

    private fun fetchWalletDetails(id: Int? = _walletId.value, forceRefresh: Boolean = false) {
        if (id == null) return
        
        // Nếu đã có data và không force refresh thì dùng cache
        if (isDataLoaded && !forceRefresh && cachedWallet != null && cachedWallet?.id == id) {
            _uiState.value = WalletDetailUiState.Success(cachedWallet!!)
            return
        }
        
        viewModelScope.launch {
            _uiState.value = WalletDetailUiState.Loading
            repository.getWalletById(id)
                .onSuccess { wallet ->
                    cachedWallet = wallet
                    isDataLoaded = true
                    _uiState.value = WalletDetailUiState.Success(wallet)
                }
                .onFailure {
                    _uiState.value = WalletDetailUiState.Error(it.message ?: "Failed to load wallet details")
                }
        }
    }
    
    fun generateInvitationCode() {
        val id = _walletId.value ?: return
        
        viewModelScope.launch {
            // Không set loading state để tránh flicker
            repository.getInvitationCode(id)
                .onSuccess { wallet ->
                    cachedWallet = wallet
                    _uiState.value = WalletDetailUiState.Success(wallet)
                }
                .onFailure {
                    _uiState.value = WalletDetailUiState.Error(it.message ?: "Failed to generate invitation code")
                }
        }
    }

    fun refresh() {
        fetchWalletDetails(forceRefresh = true)
    }
} 