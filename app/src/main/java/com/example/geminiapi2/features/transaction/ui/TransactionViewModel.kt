package com.example.geminiapi2.features.transaction.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.data.dto.TransactionSummaryResponse
import com.example.geminiapi2.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val data: TransactionSummaryResponse) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    private val _currentMonth = MutableStateFlow(LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()
    
    private val _currentYear = MutableStateFlow(LocalDate.now().year)
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()
    
    init {
        fetchTransactions()
    }
    
    fun fetchTransactions(categoryType: String? = null) {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            
            transactionRepository.getTransactionsByMonthYear(
                month = _currentMonth.value,
                year = _currentYear.value,
                categoryType = categoryType
            ).onSuccess { data ->
                _uiState.value = TransactionUiState.Success(data)
            }.onFailure { exception ->
                _uiState.value = TransactionUiState.Error(exception.message ?: "Unknown error")
            }
        }
    }
    
    fun updateMonth(month: Int, year: Int) {
        _currentMonth.value = month
        _currentYear.value = year
        fetchTransactions()
    }
} 