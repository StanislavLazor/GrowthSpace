package com.lazor.growthspace.data.model

data class User(
    val id: String = "",
    val role: String = "", // може бути "client" або "coach"
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val specialization: String = "",
    val experience: String = "",
    val price: String = "",
    val photoUrl: String = ""
)