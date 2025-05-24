package com.example.geminiapi2.features.transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.AddTransactionRequest
import com.example.geminiapi2.data.dto.AddTransactionResponse
import com.example.geminiapi2.data.dto.Message
import com.example.geminiapi2.data.dto.TransactionInfo
import com.example.geminiapi2.features.login.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiService: ApiService

) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _sendStatus = MutableStateFlow<AddTransactionState>(AddTransactionState.Idle)
    val sendStatus: StateFlow<AddTransactionState> = _sendStatus.asStateFlow()
    fun sendAddTransactionMessage(prompt: String, walletId: Int ){
        _messages.update { currentMessages->
            currentMessages.toMutableList().apply {
                add(0,Message(prompt, isFromUser =  true))
            }
        }
        viewModelScope.launch {
            try {
                _sendStatus.value = AddTransactionState.Loading
                val response = apiService.addTransaction(AddTransactionRequest(prompt, walletId))
                if (response.isSuccessful) {
                    _sendStatus.value = AddTransactionState.Success
                    response.body()?.let { addTransactionResponse ->
                        sendBotResponse(addTransactionResponse)
                    } ?: run {
                        _messages.update{ currentMessages ->
                            currentMessages.toMutableList().apply {
                                add(0,Message("Response body is null", isFromUser = false))
                            }
                        }
                    }
                } else {
                    _sendStatus.value = AddTransactionState.Idle
                    _messages.update{currentMessages ->
                        currentMessages.toMutableList().apply {
                            add(0,Message("Request failed with code: ${response.code()}", isFromUser = false))
                        }
                    }
                }
            } catch (e: Exception) {
                _messages.update{ currentMessages ->
                    currentMessages.toMutableList().apply {
                       add(0, Message("Error: ${e.message}", isFromUser = false))
                    }
                }
            }
        }
    }

    private fun formatBotResponse(response: AddTransactionResponse): String {
        // Nếu có thông tin giao dịch, trả về comment
        if (response.category.isNotEmpty()) {
            return response.comment
        } else {
            return response.comment
        }
    }

    private fun sendBotResponse(response: AddTransactionResponse) {
        if (response.category.isNotEmpty()) {
            // Nếu có thông tin giao dịch, tạo message với TransactionInfo
            val transactionInfo = TransactionInfo(
                category = response.category,
                date = response.date,
                amount = response.amount,
                categoryType = response.categoryType,
                comment = response.comment
            )
            _messages.update { currentMessages ->
                currentMessages.toMutableList().apply {
                    add(0, Message(response.comment, isFromUser = false, transactionInfo = transactionInfo))
                }
            }
        } else {
            // Nếu chỉ có comment, tạo message bình thường
            _messages.update { currentMessages ->
                currentMessages.toMutableList().apply {
                    add(0, Message(response.comment, isFromUser = false))
                }
            }
        }
    }
}
sealed class AddTransactionState {
    object Idle : AddTransactionState()
    object Loading : AddTransactionState()
    object Success : AddTransactionState()
    data class Error(val message: String) : AddTransactionState()
}