package com.lazor.growthspace.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // Весь список коучів з бази
    private val _allCoaches = MutableStateFlow<List<User>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Стан текстового пошуку
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Стан вибраної категорії (чипсу)
    private val _selectedCategory = MutableStateFlow("Усі")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // РЕАКТИВНА ФІЛЬТРАЦІЯ (Пошук + Категорія)
    val coaches: StateFlow<List<User>> = combine(
        _allCoaches,
        _searchQuery,
        _selectedCategory
    ) { coachesList, query, category ->
        var filteredList = coachesList

        // 1. Фільтруємо за категорією (якщо не "Усі")
        if (category != "Усі") {
            filteredList = filteredList.filter { coach ->
                coach.specialization.contains(category, ignoreCase = true)
            }
        }

        // 2. Фільтруємо за текстом (Ім'я АБО Спеціалізація)
        if (query.isNotBlank()) {
            filteredList = filteredList.filter { coach ->
                coach.name.contains(query, ignoreCase = true) ||
                        coach.specialization.contains(query, ignoreCase = true)
            }
        }

        filteredList
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCoaches()
    }

    private fun loadCoaches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("users")
                    .whereEqualTo("role", "coach")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("HomeVM", "Помилка завантаження", error)
                            _isLoading.value = false
                            return@addSnapshotListener
                        }

                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(User::class.java)?.copy(id = doc.id)
                        } ?: emptyList()

                        _allCoaches.value = list
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("HomeVM", "Критична помилка", e)
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }
}