package com.lazor.growthspace.data.model

data class Review(
    val id: String = "",
    val coachId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val rating: Int = 5,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val photoUrl: String = ""
)