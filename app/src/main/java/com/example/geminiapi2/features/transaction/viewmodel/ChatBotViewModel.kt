package com.example.geminiapi2.features.transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.AddTransactionRequest
import com.example.geminiapi2.data.dto.AddTransactionResponse
import com.example.geminiapi2.data.dto.Message
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
    fun sendAddTransactionMessage(prompt: String){
        _messages.update { currentMessages->
            currentMessages.toMutableList().apply {
                add(0,Message(prompt, isFromUser =  true))
            }
        }
        viewModelScope.launch {
            try {
                _sendStatus.value = AddTransactionState.Loading
                val response = apiService.addTransaction(AddTransactionRequest(prompt))
                if (response.isSuccessful) {
                    _sendStatus.value = AddTransactionState.Success
                    response.body()?.let { addTransactionResponse ->
                        _messages.update { currentMessages ->
                            currentMessages.toMutableList().apply {
                                add(0, Message(formatBotResponse(addTransactionResponse), isFromUser = false))
                            }
                        }
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

//        return """
//           Category: ${response.category}
//           Date: ${response.date}
//           Amount: ${response.amount}
//           Category Type: ${response.category_type}
//           ${response.comment}
//       """.trimIndent()
        var responseFormatted= ""
        if (response.category.isNotEmpty()){
            responseFormatted = """
           Category: ${response.category}
           Date: ${response.date}
           Amount: ${response.amount}
           Category Type: ${response.category_type}
           ${response.comment}
       """.trimIndent()
        }else{
            responseFormatted = response.comment
        }
        return responseFormatted
    }
}
sealed class AddTransactionState {
    object Idle : AddTransactionState()
    object Loading : AddTransactionState()
    object Success : AddTransactionState()
    data class Error(val message: String) : AddTransactionState()
}