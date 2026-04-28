package com.lazor.growthspace.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
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
import com.lazor.growthspace.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onChatClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    val recommendedCoaches = listOf(
        RecommendedCoach("Олександр", isOnline = true, hasStory = true),
        RecommendedCoach("Марія", isOnline = true, hasStory = false),
        RecommendedCoach("Дмитро", isOnline = false, hasStory = false),
        RecommendedCoach("Олена", isOnline = false, hasStory = false)
    )

    val chatItems = listOf(
        ChatItem("Олександр Мельник", "Чи готові ви до нашої насту...", "10:42", unreadCount = 2, isOnline = true),
        ChatItem("Олена Коваленко", "Дякую за вчорашню зустріч. Ваша...", "Вчора", unreadCount = 0, isOnline = true),
        ChatItem("Ігор Ткачук", "Я надіслав вам матеріали по та...", "Пн", unreadCount = 0, isOnline = false, isRead = true)
    )

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Пошук
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

            // 2. Рекомендовані коучі
            item {
                Text(
                    text = "РЕКОМЕНДОВАНІ КОУЧІ",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    recommendedCoaches.forEach { coach ->
                        RecommendedCoachItem(coach)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 3. Активні діалоги
            item {
                Text(
                    text = "АКТИВНІ ДІАЛОГИ",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
                )
            }

            items(chatItems) { chat ->
                ChatItemRow(chat = chat, onChatClick = onChatClick)
            }

            // 4. Архівні чати
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(SurfaceDarkElevated, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { /* TODO: Відкрити архів */ }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SurfaceDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Архівні чати", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("3", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
                }
            }
        }
    }
}

// ==========================================
// ДОПОМІЖНІ КОМПОНЕНТИ ТА МОДЕЛІ ДАНИХ
// ==========================================

data class RecommendedCoach(val name: String, val isOnline: Boolean, val hasStory: Boolean)
data class ChatItem(val name: String, val message: String, val time: String, val unreadCount: Int, val isOnline: Boolean, val isRead: Boolean = false)

@Composable
fun RecommendedCoachItem(coach: RecommendedCoach) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp)) {
        Box(contentAlignment = Alignment.Center) {
            val modifier = if (coach.hasStory) {
                Modifier
                    .size(64.dp)
                    .border(2.dp, PrimaryBlue, CircleShape)
                    .padding(4.dp)
            } else {
                Modifier.size(64.dp)
            }

            Box(
                modifier = modifier
                    .background(SurfaceDarkElevated, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(coach.name.take(1), color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            if (coach.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(0xFF00E676), CircleShape)
                        .border(2.dp, BackgroundDark, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(coach.name, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ChatItemRow(chat: ChatItem, onChatClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick(chat.name) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(SurfaceDarkElevated, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.name.take(1), color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(0xFF00E676), CircleShape)
                        .border(2.dp, BackgroundDark, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(chat.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text(chat.time, color = if (chat.unreadCount > 0) PrimaryBlue else TextGray, fontSize = 12.sp, fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chat.isRead) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = chat.message,
                    color = if (chat.unreadCount > 0) TextWhite else TextGray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        if (chat.unreadCount > 0) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.unreadCount.toString(), color = BackgroundDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}