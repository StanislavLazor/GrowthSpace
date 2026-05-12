package com.lazor.growthspace.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class AuthState {
    object Idle : AuthState() // Нічого не відбувається (початковий стан)
    object Loading : AuthState() // Крутиться кружечок завантаження
    object Success : AuthState() // Успішно зареєстровано/авторизовано
    data class Error(val message: String) : AuthState() // Помилка (наприклад, слабкий пароль)
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Стан, за яким буде стежити наш UI
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Функція реєстрації
    fun register(email: String, password: String, name: String, isCoach: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading // Показуємо завантаження

            // Викликаємо наш репозиторій
            val result = authRepository.register(email, password, name, isCoach)

            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Сталася невідома помилка")
            }
        }
    }

    // Скидання стану (щоб повідомлення про помилку не висіло вічно)
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}