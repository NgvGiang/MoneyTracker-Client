package com.example.geminiapi2.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // Có thể thêm các dependencies như Repository ở đây
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Success(
        userName = "John Doe",
        userEmail = "john.doe@gmail.com"
    ))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                // Xử lý logout - có thể thêm repository để xử lý ở đây
                _uiState.value = SettingsUiState.LoggedOut
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Logout failed")
            }
        }
    }
}

sealed class SettingsUiState {
    data class Success(
        val userName: String,
        val userEmail: String
    ) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
    object Loading : SettingsUiState()
    object LoggedOut : SettingsUiState()
} 