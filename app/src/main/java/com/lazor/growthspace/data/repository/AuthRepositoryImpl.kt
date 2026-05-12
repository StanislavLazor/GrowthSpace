package com.lazor.growthspace.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    // Колекція в базі даних
    private val usersCollection = firestore.collection("users")

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        isCoach: Boolean
    ): Result<User> {
        return try {
            // 1. Створюємо користувача в Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            // 2. Створюємо об'єкт нашого юзера
            val role = if (isCoach) "coach" else "client"
            val newUser = User(
                id = userId,
                role = role,
                name = name,
                email = email
            )

            // 3. Зберігаємо дані в Firestore
            usersCollection.document(userId).set(newUser).await()

            // Повертаємо успішний результат
            Result.success(newUser)
        } catch (e: Exception) {
            // Якщо щось пішло не так (наприклад, такий email вже є)
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // 1. Логінимося в Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            // 2. Дістаємо дані юзера з Firestore
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
                ?: throw Exception("User data not found in database")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}