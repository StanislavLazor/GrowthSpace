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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen( // <-- Змінили назву тут
    coachName: String,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Імітація індивідуальних повідомлень залежно від обраного коуча
    val messages = remember(coachName) {
        if (coachName.contains("Олександр")) {
            listOf(
                MessageItem("Привіт! Як успіхи з останнім завданням по тайм-менеджменту?", "10:42", false, "Олександр", true),
                MessageItem("Сьогодні", isSeparator = true),
                MessageItem("Добрий ранок! Вже краще, матриця Ейзенхауера дійсно допомагає пріоритезувати.", "10:45", true, "Ви"),
                MessageItem("Чудово. Чи готові ви до нашої наступної сесії? У мене є кілька ідей щодо вашого плану розвитку.", "10:42", false, "Олександр", true)
            )
        } else {
            listOf(
                MessageItem("Вітаю! Це індивідуальний чат. Я $coachName, ваш коуч. Чим можу допомогти?", "10:00", false, coachName, true)
            )
        }
    }

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).background(SurfaceDarkElevated, CircleShape)) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = TextWhite)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(40.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                        Text(coachName.take(1), color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF00E676), CircleShape).border(2.dp, BackgroundDark, CircleShape))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(coachName, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Лідерство & Управління", color = PrimaryBlue, fontSize = 12.sp)
                }

                IconButton(onClick = onProfileClick, modifier = Modifier.size(40.dp).background(SurfaceDarkElevated, CircleShape)) {
                    Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = TextWhite)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isSeparator) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(SurfaceDarkElevated))
                            Text(msg.text, color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp))
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(SurfaceDarkElevated))
                        }
                    } else {
                        ChatMessageBubble(msg)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                    ActionChip(icon = Icons.Default.Add, text = "Файли")
                    ActionChip(icon = Icons.Outlined.Image, text = "Зображення")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Напишіть повідомлення...", color = TextGray) },
                        trailingIcon = { Icon(Icons.Outlined.Mood, contentDescription = null, tint = TextGray) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDarkElevated,
                            unfocusedContainerColor = SurfaceDarkElevated,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier.size(48.dp).background(PrimaryBlue, CircleShape).clickable { /* TODO: Відправити */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = BackgroundDark)
                    }
                }
            }
        }
    }
}

data class MessageItem(val text: String, val time: String = "", val isOut: Boolean = false, val senderName: String = "", val showAvatar: Boolean = false, val isSeparator: Boolean = false)

@Composable
fun ActionChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.border(1.dp, SurfaceDarkElevated, RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 8.dp).clickable {  },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = TextGray, fontSize = 13.sp)
    }
}

@Composable
fun ChatMessageBubble(msg: MessageItem) {
    val bubbleColor = if (msg.isOut) Color(0xFF162B44) else SurfaceDarkElevated
    val shape = if (msg.isOut) RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (msg.isOut) Alignment.End else Alignment.Start) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!msg.isOut && msg.showAvatar) {
                Box(modifier = Modifier.size(28.dp).background(SurfaceDark, CircleShape).padding(bottom = 4.dp), contentAlignment = Alignment.Center) {
                    Text(msg.senderName.take(1), color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(horizontalAlignment = if (msg.isOut) Alignment.End else Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!msg.isOut) {
                        Text(msg.senderName, color = TextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg.time, color = TextGray, fontSize = 10.sp)
                    } else {
                        Text(msg.time, color = TextGray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg.senderName, color = TextGray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.background(bubbleColor, shape).padding(16.dp).widthIn(max = 280.dp)) {
                    Text(msg.text, color = TextWhite, fontSize = 15.sp, lineHeight = 22.sp)
                }

                if (msg.isOut) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}