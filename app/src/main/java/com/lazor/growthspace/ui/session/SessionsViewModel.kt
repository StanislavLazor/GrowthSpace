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

    // Стейт для зберігання вільних слотів конкретного коуча
    private val _coachAvailableSlots = MutableStateFlow<List<SessionBooking>>(emptyList())
    val coachAvailableSlots: StateFlow<List<SessionBooking>> = _coachAvailableSlots.asStateFlow()

    init {
        loadUserDataAndSessions()
    }

    private fun loadUserDataAndSessions() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch

                // 1. Отримуємо дані користувача (щоб знати роль та ім'я)
                val userDoc = firestore.collection("users").document(userId).get().await()
                val user = userDoc.toObject(User::class.java)

                _state.value = _state.value.copy(currentUser = user)

                // 2. Слухаємо сесії в реальному часі залежно від ролі
                if (user?.role == "coach") {
                    firestore.collection("sessions")
                        .whereEqualTo("coachId", userId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("SessionsVM", "Помилка завантаження сесій коуча", error)
                                return@addSnapshotListener
                            }
                            val sessions = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(SessionBooking::class.java)?.copy(id = doc.id)
                            } ?: emptyList()
                            _state.value = _state.value.copy(
                                sessions = sessions.sortedByDescending { it.date },
                                isLoading = false
                            )
                        }
                } else {
                    firestore.collection("sessions")
                        .whereEqualTo("clientId", userId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("SessionsVM", "Помилка завантаження сесій клієнта", error)
                                return@addSnapshotListener
                            }
                            val sessions = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(SessionBooking::class.java)?.copy(id = doc.id)
                            } ?: emptyList()
                            _state.value = _state.value.copy(
                                sessions = sessions.sortedByDescending { it.date },
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

    // Завантажуємо вільні слоти для екранів календаря та часу
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

    // [ТІЛЬКИ ДЛЯ КОУЧА] Створення нового вільного слоту
    fun createAvailableSlot(date: String, time: String) {
        viewModelScope.launch {
            try {
                val user = _state.value.currentUser ?: return@launch
                if (user.role != "coach") return@launch

                val newSlot = SessionBooking(
                    id = UUID.randomUUID().toString(),
                    coachId = "1",
                    coachName = user.name,
                    date = date,
                    time = time,
                    status = "available"
                )

                firestore.collection("sessions").document(newSlot.id).set(newSlot).await()
                Log.d("SessionsVM", "Слот успішно створено: ${newSlot.id}")
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка створення слоту", e)
            }
        }
    }

    // [ТІЛЬКИ ДЛЯ КЛІЄНТА] Створення запиту на нову сесію (з екрана підтвердження)
    fun requestSessionAsClient(coachId: String, coachName: String, date: String, time: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val user = _state.value.currentUser ?: return@launch

                // Перевіряємо, чи існує вже вільний слот на цей час
                val availableDocs = firestore.collection("sessions")
                    .whereEqualTo("coachId", coachId)
                    .whereEqualTo("date", date)
                    .whereEqualTo("time", time)
                    .whereEqualTo("status", "available")
                    .get().await()

                if (!availableDocs.isEmpty) {
                    // Якщо слот є, оновлюємо його (Клієнт займає його)
                    val slotId = availableDocs.documents.first().id
                    val updates = mapOf(
                        "clientId" to user.id,
                        "clientName" to user.name,
                        "status" to "pending"
                    )
                    firestore.collection("sessions").document(slotId).update(updates).await()
                } else {
                    // Якщо немає (клієнт пропонує свій час), створюємо новий
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

    // [ТІЛЬКИ ДЛЯ КЛІЄНТА] Бронювання вже існуючого вільного слоту
    fun bookSlot(sessionId: String) {
        if (sessionId.isEmpty()) return

        viewModelScope.launch {
            try {
                val user = _state.value.currentUser ?: return@launch
                val updates = mapOf(
                    "clientId" to user.id,
                    "clientName" to user.name,
                    "status" to "pending"
                )
                firestore.collection("sessions").document(sessionId).update(updates).await()
                Log.d("SessionsVM", "Слот заброньовано: $sessionId")
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка бронювання", e)
            }
        }
    }

    // [ТІЛЬКИ ДЛЯ КОУЧА] Підтвердження або скасування сесії
    fun updateSessionStatus(sessionId: String, newStatus: String) {
        if (sessionId.isEmpty()) return

        viewModelScope.launch {
            try {
                firestore.collection("sessions").document(sessionId).update("status", newStatus).await()
                Log.d("SessionsVM", "Статус змінено на $newStatus для $sessionId")
            } catch (e: Exception) {
                Log.e("SessionsVM", "Помилка оновлення статусу", e)
            }
        }
    }
}