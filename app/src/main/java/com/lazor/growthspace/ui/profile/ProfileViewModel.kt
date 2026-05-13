package com.lazor.growthspace.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListeningToProfile()
    }

    private fun startListeningToProfile() {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            listenerRegistration = firestore.collection("users").document(userId)
                .addSnapshotListener { document, error ->
                    if (error != null) {
                        _profileState.value = ProfileState.Error(error.message ?: "Помилка")
                        return@addSnapshotListener
                    }
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            _profileState.value = ProfileState.Success(user)
                        }
                    }
                }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}