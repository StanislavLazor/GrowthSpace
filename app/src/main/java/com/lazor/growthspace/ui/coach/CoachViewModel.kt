package com.lazor.growthspace.ui.coach

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.Review
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CoachViewModel(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _coach = MutableStateFlow<User?>(null)
    val coach: StateFlow<User?> = _coach.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCurrentUserData()
    }

    private fun loadCurrentUserData() {
        viewModelScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUserId() ?: return@launch
                val doc = firestore.collection("users").document(currentUserId).get().await()
                _currentUser.value = doc.toObject(User::class.java)?.copy(id = doc.id)
            } catch (e: Exception) {
                Log.e("CoachVM", "Error loading current user", e)
            }
        }
    }

    fun loadCoachData(coachId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("users").document(coachId).get().await()
                _coach.value = doc.toObject(User::class.java)?.copy(id = doc.id)
                loadReviews(coachId)
            } catch (e: Exception) {
                Log.e("CoachVM", "Error loading coach data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadReviews(coachId: String) {
        firestore.collection("reviews")
            .whereEqualTo("coachId", coachId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Review::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _reviews.value = list.sortedByDescending { it.timestamp }
            }
    }

    fun addReview(coachId: String, rating: Int, text: String) {
        val user = _currentUser.value ?: return

        if (user.role == "coach" || user.id == coachId) {
            Log.d("CoachVM", "Review blocked: Coach cannot review themselves or others")
            return
        }

        viewModelScope.launch {
            try {
                val review = Review(
                    id = UUID.randomUUID().toString(),
                    coachId = coachId,
                    clientId = user.id,
                    clientName = user.name,
                    rating = rating,
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                firestore.collection("reviews").document(review.id).set(review).await()
            } catch (e: Exception) {
                Log.e("CoachVM", "Error adding review", e)
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("reviews").document(reviewId).delete().await()
                Log.d("CoachVM", "Відгук успішно видалено: $reviewId")
            } catch (e: Exception) {
                Log.e("CoachVM", "Помилка при видаленні відгуку", e)
            }
        }
    }
}