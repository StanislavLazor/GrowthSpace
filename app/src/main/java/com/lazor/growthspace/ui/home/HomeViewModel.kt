package com.lazor.growthspace.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // Весь список коучів з бази
    private var allCoaches = listOf<User>()

    // Стан для UI (відфільтрований список)
    private val _coaches = MutableStateFlow<List<User>>(emptyList())
    val coaches: StateFlow<List<User>> = _coaches.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCoaches()
    }

    private fun loadCoaches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Тягнемо тільки тих, у кого роль "coach"
                val snapshot = firestore.collection("users")
                    .whereEqualTo("role", "coach")
                    .get()
                    .await()

                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(id = doc.id)
                }

                allCoaches = list
                _coaches.value = list // Спочатку показуємо всіх
            } catch (e: Exception) {
                // Обробка помилки
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Логіка пошуку
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _coaches.value = allCoaches
        } else {
            // Шукаємо за ім'ям (ігноруючи регістр)
            _coaches.value = allCoaches.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
}