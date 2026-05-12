package com.lazor.growthspace.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    // Тягнемо дані юзера з Firestore
                    val document = firestore.collection("users").document(userId).get().await()

                    val user = document.toObject(User::class.java) ?: User(
                        id = userId,
                        name = document.getString("name") ?: "Користувач",
                        email = document.getString("email") ?: "",
                        role = document.getString("role") ?: "client"
                    )

                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error("Користувача не знайдено")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Помилка завантаження")
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}