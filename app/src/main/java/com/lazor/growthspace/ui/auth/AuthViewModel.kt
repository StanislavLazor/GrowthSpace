package com.lazor.growthspace.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun register(email: String, password: String, name: String, isCoach: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, password, name, isCoach)
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Сталася невідома помилка")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Невірний email або пароль")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _authState.value = AuthState.Error("Будь ласка, введіть ваш Email у поле вище")
                return@launch
            }

            _authState.value = AuthState.Loading

            // Запит виконується строго через абстракцію репозиторію
            val result = authRepository.resetPassword(email)

            result.onSuccess {
                _authState.value = AuthState.PasswordResetSent
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Помилка відновлення пароля")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}