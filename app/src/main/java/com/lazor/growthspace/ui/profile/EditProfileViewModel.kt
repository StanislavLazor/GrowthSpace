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

sealed class EditProfileState {
    object Loading : EditProfileState()
    data class Success(val user: User) : EditProfileState()
    object Saved : EditProfileState() // Стан для успішного збереження
    data class Error(val message: String) : EditProfileState()
}

class EditProfileViewModel(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow<EditProfileState>(EditProfileState.Loading)
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: throw Exception("User not logged in")
                val doc = firestore.collection("users").document(userId).get().await()

                val user = doc.toObject(User::class.java) ?: User(
                    id = userId,
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "client",
                    bio = doc.getString("bio") ?: "" // Завантажуємо біо
                )
                _state.value = EditProfileState.Success(user)
            } catch (e: Exception) {
                _state.value = EditProfileState.Error(e.message ?: "Помилка завантаження")
            }
        }
    }

    // Тепер приймаємо два параметри
    fun saveProfile(newName: String, newBio: String) {
        viewModelScope.launch {
            _state.value = EditProfileState.Loading
            try {
                val userId = authRepository.getCurrentUserId() ?: throw Exception("User not logged in")

                // Оновлюємо декілька полів одночасно
                val updates = hashMapOf<String, Any>(
                    "name" to newName,
                    "bio" to newBio
                )

                firestore.collection("users").document(userId)
                    .update(updates)
                    .await()

                _state.value = EditProfileState.Saved
            } catch (e: Exception) {
                _state.value = EditProfileState.Error(e.message ?: "Помилка збереження")
            }
        }
    }
}