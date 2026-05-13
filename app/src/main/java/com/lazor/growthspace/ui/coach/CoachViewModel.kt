package com.lazor.growthspace.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CoachViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _coach = MutableStateFlow<User?>(null)
    val coach: StateFlow<User?> = _coach.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCoachData(coachId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("users").document(coachId).get().await()
                _coach.value = doc.toObject(User::class.java)?.copy(id = doc.id)
            } catch (e: Exception) {
                // Можна додати стан помилки
            } finally {
                _isLoading.value = false
            }
        }
    }
}