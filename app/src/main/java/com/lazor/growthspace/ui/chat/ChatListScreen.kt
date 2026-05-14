package com.lazor.growthspace.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lazor.growthspace.data.model.Message
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.ui.components.UserAvatar
import com.lazor.growthspace.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onChatClick: (String, String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val otherUsersState = remember { mutableStateOf<List<User>>(emptyList()) }
    val lastMessages = remember { mutableStateMapOf<String, Message>() }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isEmpty()) return@LaunchedEffect

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val allUsers = snapshot.toObjects(User::class.java)
                val filteredUsers = allUsers.filter { it.id != currentUserId }
                otherUsersState.value = filteredUsers
                isLoading.value = false

                filteredUsers.forEach { user ->
                    val chatId = if (currentUserId < user.id) "${currentUserId}_${user.id}" else "${user.id}_${currentUserId}"

                    db.collection("chats").document(chatId).collection("messages")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1)
                        .addSnapshotListener { msgSnapshot, _ ->
                            val lastMsg = msgSnapshot?.documents?.firstOrNull()?.toObject(Message::class.java)
                            if (lastMsg != null) {
                                lastMessages[user.id] = lastMsg
                            }
                        }
                }
            }
            .addOnFailureListener {
                isLoading.value = false
            }
    }

    val sortedActiveDialogs = otherUsersState.value.sortedByDescending { user ->
        lastMessages[user.id]?.timestamp ?: 0L
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Повідомлення", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(vertical = 8.dp)
                            .height(52.dp),
                        placeholder = { Text("Пошук коучів або чатів...", color = TextGray, fontSize = 15.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                        shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDarkElevated,
                            unfocusedContainerColor = SurfaceDarkElevated,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "РЕКОМЕНДОВАНІ",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        otherUsersState.value.take(4).forEach { user ->
                            RecommendedCoachItem(
                                user = user,
                                onChatClick = onChatClick
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    Text(
                        text = "АКТИВНІ ДІАЛОГИ",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
                    )
                }

                items(sortedActiveDialogs) { user ->
                    val lastMsg = lastMessages[user.id]

                    val timeString = if (lastMsg != null) {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lastMsg.timestamp))
                    } else ""

                    val messageText = lastMsg?.text ?: "Натисніть, щоб почати діалог"
                    val isUnread = lastMsg != null && !lastMsg.isRead && lastMsg.senderId != currentUserId

                    ChatItemRow(
                        name = user.name,
                        id = user.id,
                        photoUrl = user.photoUrl, // Передаємо нове поле!
                        lastMessage = messageText,
                        time = timeString,
                        isUnread = isUnread,
                        onChatClick = onChatClick
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ArchiveButton()
                }
            }
        }
    }
}

@Composable
fun RecommendedCoachItem(user: User, onChatClick: (String, String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onChatClick(user.name, user.id) }
    ) {
        UserAvatar(
            photoUrl = user.photoUrl,
            name = user.name,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.name,
            color = TextWhite,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChatItemRow(
    name: String,
    id: String,
    photoUrl: String,
    lastMessage: String,
    time: String,
    isUnread: Boolean,
    onChatClick: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick(name, id) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            photoUrl = photoUrl,
            name = name,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = time,
                    color = if (isUnread) PrimaryBlue else TextGray,
                    fontSize = 12.sp,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lastMessage,
                color = if (isUnread) TextWhite else TextGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
            )
        }

        if (isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(PrimaryBlue, CircleShape)
            )
        }
    }
}

@Composable
fun ArchiveButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(SurfaceDarkElevated, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { /* TODO */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Archive, contentDescription = null, tint = TextGray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Архівні чати",
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
    }
}