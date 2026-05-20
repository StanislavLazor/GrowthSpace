package com.lazor.growthspace.ui.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.SessionBooking
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class SessionsState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val sessions: List<SessionBooking> = emptyList()
)

class SessionsViewModel(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(SessionsState())
    val state: StateFlow<SessionsState> = _state.asStateFlow()

    private val _coachAvailableSlots = MutableStateFlow<List<SessionBooking>>(emptyList())
    val coachAvailableSlots: StateFlow<List<SessionBooking>> = _coachAvailableSlots.asStateFlow()

    init {
        loadUserDataAndSessions()
    }

    private fun loadUserDataAndSessions() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId == null) {
                    _state.value = _state.value.copy(isLoading = false)
                    return@launch
                }

                val userDoc = firestore.collection("users").document(userId).get().await()
                val user = userDoc.toObject(User::class.java)

                _state.value = _state.value.copy(currentUser = user)

                val queryField = if (user?.role == "coach") "coachId" else "clientId"

                firestore.collection("sessions")
                    .whereEqualTo(queryField, userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("SessionsVM", "Помилка завантаження сесій", error)
                            _state.value = _state.value.copy(isLoading = false)
                            return@addSnapshotListener
                        }

                        val rawSessions = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(SessionBooking::class.java)?.copy(id = doc.id)
                        } ?: emptyList()

                        viewModelScope.launch {
                            val userIdsToFetch = rawSessions
                                .flatMap { listOf(it.coachId, it.clientId) }
                                .filter { it.isNotEmpty() }
                                .toSet()

                            val userPhotosMap = mutableMapOf<String, String?>()

                            for (id in userIdsToFetch) {
                                try {
                                    val doc = firestore.collection("users").document(id).get().await()
                                    val rawUrl = doc.getString("photoUrl") ?: ""
                                    userPhotosMap[id] = rawUrl.replace("\"", "").trim()
                                } catch (e: Exception) {
                                    userPhotosMap[id] = ""
                                }
                            }

                            val enrichedSessions = rawSessions.map { session ->
                                session.copy(
                                    coachPhotoUrl = userPhotosMap[session.coachId] ?: "",
                                    clientPhotoUrl = userPhotosMap[session.clientId] ?: ""
                                )
                            }

                            val sortedSessions = enrichedSessions.sortedWith(
                                compareByDescending<SessionBooking> { it.date }.thenByDescending { it.time }
                            )

                            _state.value = _state.value.copy(
                                sessions = sortedSessions,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка ініціалізації", e)
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun saveSessionNotes(sessionId: String, notes: String, privateNotes: String? = null) {
        viewModelScope.launch {
            try {
                val updates = mutableMapOf<String, Any>("notes" to notes)
                privateNotes?.let { updates["privateNotes"] = it }
                firestore.collection("sessions").document(sessionId).update(updates).await()
            } catch (e: Exception) { Log.e("SessionsVM", "Error", e) }
        }
    }

    fun updateSessionStatus(sessionId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                firestore.collection("sessions").document(sessionId).update("status", newStatus).await()
            } catch (e: Exception) { Log.e("SessionsVM", "Error", e) }
        }
    }

    fun loadAvailableSlotsForCoach(coachId: String) {
        viewModelScope.launch {
            firestore.collection("sessions")
                .whereEqualTo("coachId", coachId)
                .whereEqualTo("status", "available")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val slots = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(SessionBooking::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    _coachAvailableSlots.value = slots
                }
        }
    }

    fun createAvailableSlot(date: String, time: String) {
        viewModelScope.launch {
            try {
                val user = _state.value.currentUser ?: return@launch
                if (user.role != "coach") return@launch
                val newSlot = SessionBooking(
                    id = UUID.randomUUID().toString(),
                    coachId = user.id,
                    coachName = user.name,
                    date = date,
                    time = time,
                    status = "available"
                )
                firestore.collection("sessions").document(newSlot.id).set(newSlot).await()
            } catch (e: Exception) { Log.e("SessionsVM", "Error", e) }
        }
    }

    fun requestSessionAsClient(coachId: String, coachName: String, date: String, time: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val user = _state.value.currentUser ?: return@launch
                val availableDocs = firestore.collection("sessions")
                    .whereEqualTo("coachId", coachId)
                    .whereEqualTo("date", date)
                    .whereEqualTo("time", time)
                    .whereEqualTo("status", "available")
                    .get().await()

                if (!availableDocs.isEmpty) {
                    val slotId = availableDocs.documents.first().id
                    val updates = mapOf("clientId" to user.id, "clientName" to user.name, "status" to "pending")
                    firestore.collection("sessions").document(slotId).update(updates).await()
                } else {
                    val newSession = SessionBooking(
                        id = UUID.randomUUID().toString(),
                        coachId = coachId,
                        coachName = coachName,
                        clientId = user.id,
                        clientName = user.name,
                        date = date,
                        time = time,
                        status = "pending"
                    )
                    firestore.collection("sessions").document(newSession.id).set(newSession).await()
                }
                onComplete()
            } catch (e: Exception) { Log.e("SessionsVM", "Error", e) }
        }
    }
}