package com.lazor.growthspace.data.model

data class User(
    val id: String = "",
    val role: String = "client", // може бути "client" або "coach"
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val bio: String = ""
)