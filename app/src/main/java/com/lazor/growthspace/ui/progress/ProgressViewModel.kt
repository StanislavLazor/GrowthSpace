package com.lazor.growthspace.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.Goal
import com.lazor.growthspace.data.model.SubTask
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProgressViewModel(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            firestore.collection("goals")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, _ ->
                    val goalList = snapshot?.documents?.mapNotNull { it.toObject(Goal::class.java)?.copy(id = it.id) }
                    // Сортуємо від найновіших до найстаріших
                    _goals.value = goalList?.sortedByDescending { it.createdAt } ?: emptyList()
                }
        }
    }

    // Зміна стану завдання (Галочка)
    fun toggleTask(goalId: String, taskId: String, isNowCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val goal = _goals.value.find { it.id == goalId } ?: return@launch
                val updatedTasks = goal.tasks.map {
                    if (it.id == taskId) it.copy(isCompleted = isNowCompleted) else it
                }
                firestore.collection("goals").document(goalId).update("tasks", updatedTasks).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Створення нової цілі
    fun addGoal(title: String, description: String, taskTitles: List<String>) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch
                val subTasks = taskTitles.filter { it.isNotBlank() }.map {
                    SubTask(id = UUID.randomUUID().toString(), title = it, isCompleted = false)
                }
                val newGoal = Goal(
                    userId = userId, title = title, description = description,
                    tasks = subTasks, createdAt = System.currentTimeMillis()
                )
                firestore.collection("goals").add(newGoal).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Видалення цілі (смітник)
    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("goals").document(goalId).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}