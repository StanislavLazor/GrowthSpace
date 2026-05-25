package com.lazor.growthspace.data.repository

import com.lazor.growthspace.data.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, name: String, isCoach: Boolean): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    fun logout()
    fun getCurrentUserId(): String?
}
