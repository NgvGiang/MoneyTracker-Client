package com.example.geminiapi2.features.wallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.data.dto.WalletResponse
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<WalletDetailUiState>(WalletDetailUiState.Loading)
    val uiState: StateFlow<WalletDetailUiState> = _uiState.asStateFlow()

    private var _walletId = MutableStateFlow<Int?>(null)
    val walletId: StateFlow<Int?> = _walletId.asStateFlow()

    init {
        savedStateHandle.get<Int>("walletId")?.let { id ->
            setWalletId(id)
        }
    }

    fun setWalletId(id: Int) {
        _walletId.value = id
        fetchWalletDetails(id)
    }

    private fun fetchWalletDetails(id: Int? = _walletId.value) {
        if (id == null) return
        
        viewModelScope.launch {
            _uiState.value = WalletDetailUiState.Loading
            repository.getWalletById(id)
                .onSuccess {
                    _uiState.value = WalletDetailUiState.Success(it)
                }
                .onFailure {
                    _uiState.value = WalletDetailUiState.Error(it.message ?: "Failed to load wallet details")
                }
        }
    }
    
    fun generateInvitationCode() {
        val id = _walletId.value ?: return
        
        viewModelScope.launch {
            _uiState.value = WalletDetailUiState.Loading
            repository.getInvitationCode(id)
                .onSuccess {
                    _uiState.value = WalletDetailUiState.Success(it)
                }
                .onFailure {
                    _uiState.value = WalletDetailUiState.Error(it.message ?: "Failed to generate invitation code")
                }
        }
    }
} 