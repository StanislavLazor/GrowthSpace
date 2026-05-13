package com.lazor.growthspace.data.model

data class SessionBooking(
    val id: String = "",
    val coachId: String = "",
    val coachName: String = "", // Щоб не робити зайвих запитів до БД
    val clientId: String = "",  // Порожній, поки слот ніхто не забронював
    val clientName: String = "",
    val date: String = "",      // Формат "2026-05-20"
    val time: String = "",      // Формат "14:00"
    val status: String = "available", // available, pending, confirmed, cancelled, completed
    val createdAt: Long = System.currentTimeMillis()
)