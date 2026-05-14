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

                // ЗАПОБІЖНИК 1: Якщо користувача немає, вимикаємо лоадер
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
                    // ВИДАЛЕНО .orderBy() -> Тепер Firebase не буде вимагати індексів!
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("SessionsVM", "Помилка завантаження сесій", error)
                            // ЗАПОБІЖНИК 2: Вимикаємо лоадер при помилці
                            _state.value = _state.value.copy(isLoading = false)
                            return@addSnapshotListener
                        }

                        val sessions = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(SessionBooking::class.java)?.copy(id = doc.id)
                        } ?: emptyList()

                        // СОРТУЄМО ЛОКАЛЬНО: Спочатку за датою, потім за часом (найновіші зверху)
                        val sortedSessions = sessions.sortedWith(
                            compareByDescending<SessionBooking> { it.date }.thenByDescending { it.time }
                        )

                        _state.value = _state.value.copy(
                            sessions = sortedSessions,
                            isLoading = false // Успішне завантаження
                        )
                    }
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка ініціалізації", e)
                // ЗАПОБІЖНИК 3: Вимикаємо лоадер, якщо впало щось інше
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    // КЕРУВАННЯ НОТАТКАМИ
    fun saveSessionNotes(sessionId: String, notes: String, privateNotes: String? = null) {
        if (sessionId.isEmpty()) return

        viewModelScope.launch {
            try {
                val updates = mutableMapOf<String, Any>("notes" to notes)
                // Приватні нотатки додаємо лише якщо вони передані (тільки для коуча)
                privateNotes?.let { updates["privateNotes"] = it }

                firestore.collection("sessions").document(sessionId)
                    .update(updates)
                    .await()
                Log.d("SessionsVM", "Нотатки успішно збережено для $sessionId")
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка збереження нотаток", e)
            }
        }
    }

    // ОНОВЛЕННЯ СТАТУСУ (Підтвердити, Скасувати, Завершити)
    fun updateSessionStatus(sessionId: String, newStatus: String) {
        if (sessionId.isEmpty()) return

        viewModelScope.launch {
            try {
                firestore.collection("sessions").document(sessionId)
                    .update("status", newStatus)
                    .await()
                Log.d("SessionsVM", "Статус змінено на $newStatus для $sessionId")
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка оновлення статусу", e)
            }
        }
    }

    // РОБОТА ЗІ СЛОТАМИ
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
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка створення слоту", e)
            }
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
                    val updates = mapOf(
                        "clientId" to user.id,
                        "clientName" to user.name,
                        "status" to "pending"
                    )
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
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка створення запиту", e)
            }
        }
    }
}