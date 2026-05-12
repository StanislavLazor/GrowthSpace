package com.lazor.growthspace.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        isCoach: Boolean
    ): Result<User> {
        return try {
            // 1. Реєстрація в Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Не вдалося отримати UID користувача")

            val role = if (isCoach) "coach" else "client"

            // 2. Створюємо Map для Firestore (це надійніше, ніж кидати об'єкт класу)
            // Так ми точно знаємо, які ключі будуть у базі
            val userMap = hashMapOf(
                "id" to userId,
                "name" to name,
                "email" to email,
                "role" to role,
                "createdAt" to com.google.firebase.Timestamp.now() // Корисно для сортування пізніше
            )

            // 3. Запис у базу
            usersCollection.document(userId).set(userMap).await()

            // 4. Повертаємо об'єкт User для додатка
            Result.success(User(id = userId, name = name, email = email, role = role))

        } catch (e: Exception) {
            // Якщо Auth створився, а Firestore впав — видаляємо юзера з Auth,
            // щоб він не висів "мертвим вантажем" без даних у БД
            if (auth.currentUser != null && e !is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                auth.currentUser?.delete()?.await()
            }
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("UID не знайдено")

            val document = usersCollection.document(userId).get().await()

            // Використовуємо безпечне отримання полів, якщо toObject підведе
            val user = document.toObject(User::class.java) ?: User(
                id = userId,
                name = document.getString("name") ?: "Користувач",
                email = document.getString("email") ?: email,
                role = document.getString("role") ?: "client"
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
}