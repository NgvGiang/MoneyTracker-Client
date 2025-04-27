package com.example.geminiapi2.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    private val _logoutState = MutableStateFlow<Boolean>(false) // Để thông báo khi logout hoàn tất
    val logoutState: StateFlow<Boolean> = _logoutState
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage
    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _logoutState.value = true
            } catch (e: Exception) {
                _snackbarMessage.value = "Logout failed: ${e.message}"
            }
        }
    }
    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            tokenManager.getTokenFlow().collect { token ->
                _loginState.value = if (token.isNullOrEmpty()) LoginState.None else LoginState.Success
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {

//            if (username.isEmpty() || password.isEmpty()) {
//                _snackbarMessage.value = "Please enter both username and password"
//                return@launch
//            }
            try {
                _loginState.value = LoginState.Loading
                val response = authRepository.login(username, password)
                response.onSuccess {
                    _loginState.value = LoginState.Success
                }.onFailure {
                    _loginState.value = LoginState.Error(it.message ?: "Unknown error")
                    _snackbarMessage.value = it.message ?: "Log fail"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error: ${e.message}"
            }
        }
    }
    fun register(username: String, email: String, password: String,repassword: String) {
        viewModelScope.launch {
            try{

                _loginState.value = LoginState.Loading
                if (password != repassword){
                    _loginState.value = LoginState.Error("Passwords do not match")
                    _snackbarMessage.value = "Passwords do not match"
                    return@launch
                }
                val response = authRepository.register(username, email, password)
                response.onSuccess {
                    _loginState.value = LoginState.Success
                }.onFailure {
                    _loginState.value = LoginState.Error(it.message ?: "Unknown error")
                    _snackbarMessage.value = it.message ?: "Log fail"
                }
            }catch (e: Exception) {
                _snackbarMessage.value = "Error: ${e.message}"
            }

        }
    }


    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    object None : LoginState()
    data class Error(val message: String) : LoginState()
}
