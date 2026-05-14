package com.lazor.growthspace.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lazor.growthspace.data.model.Message
import com.lazor.growthspace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val currentUserId: String
        get() = authRepository.getCurrentUserId() ?: ""

    /**
     * Починає слухати повідомлення в реальному часі між поточним користувачем та іншим (коучем/клієнтом)
     */
    fun listenForMessages(otherUserId: String) {
        if (currentUserId.isEmpty() || otherUserId.isEmpty()) return

        // Формуємо стабільний ID чату: сортуємо ID користувачів за алфавітом
        val chatId = if (currentUserId < otherUserId) {
            "${currentUserId}_$otherUserId"
        } else {
            "${otherUserId}_$currentUserId"
        }

        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatVM", "Помилка слухача чату", error)
                    return@addSnapshotListener
                }

                val newMessages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _messages.value = newMessages
            }
    }

    /**
     * Відправка повідомлення
     */
    fun sendMessage(receiverId: String, text: String) {
        if (text.isBlank() || currentUserId.isEmpty()) return

        val chatId = if (currentUserId < receiverId) {
            "${currentUserId}_$receiverId"
        } else {
            "${receiverId}_$currentUserId"
        }

        val messageId = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document().id

        val newMessage = Message(
            id = messageId,
            senderId = currentUserId,
            receiverId = receiverId,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            try {
                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .document(messageId)
                    .set(newMessage)
            } catch (e: Exception) {
                Log.e("ChatVM", "Помилка відправки повідомлення", e)
            }
        }
    }
}