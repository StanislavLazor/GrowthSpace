package com.lazor.growthspace.data.model

import com.google.firebase.firestore.PropertyName


data class Goal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val tasks: List<SubTask> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class SubTask(
    val id: String = "",
    val title: String = "",
    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false
)
