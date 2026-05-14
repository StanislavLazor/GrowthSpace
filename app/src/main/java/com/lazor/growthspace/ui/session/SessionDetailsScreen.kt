package com.lazor.growthspace.ui.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.data.model.SessionBooking // ВИПРАВЛЕНО: Використовуємо твою модель!
import com.lazor.growthspace.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    session: SessionBooking, // ВИПРАВЛЕНО
    isCoach: Boolean,
    otherUserName: String,
    onBackClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onSaveNotes: (String, String) -> Unit
) {
    // Делегат by потребує імпортів getValue та setValue (вони вже підключені через androidx.compose.runtime.*)
    var notesText by remember { mutableStateOf(session.notes) }
    var privateNotesText by remember { mutableStateOf(session.privateNotes) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Деталі сесії", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Блок інформації про сесію
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCoach) "Клієнт: $otherUserName" else "Коуч: $otherUserName",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // ВИПРАВЛЕНО: Беремо дату і час напряму з SessionBooking
                    Text("Дата: ${session.date}", color = TextGray, fontSize = 14.sp)
                    Text("Час: ${session.time}", color = TextGray, fontSize = 14.sp)
                    Text("Тривалість: ${session.durationMin} хв", color = TextGray, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Відображення статусу (Колір залежить від статусу)
                    val statusColor = when(session.status) {
                        "pending" -> Color(0xFFFFA000) // Оранжевий
                        "confirmed" -> Color(0xFF4CAF50) // Зелений
                        "completed" -> PrimaryBlue
                        "canceled" -> Color(0xFFE53935) // Червоний
                        else -> TextGray
                    }
                    val statusText = when(session.status) {
                        "pending" -> "Очікує підтвердження"
                        "confirmed" -> "Підтверджено"
                        "completed" -> "Завершено"
                        "canceled" -> "Скасовано"
                        else -> session.status
                    }
                    Text(
                        text = "Статус: $statusText",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Загальні нотатки (Видимі для обох)
            Text("Нотатки до сесії", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Запишіть, що ви обговорили...", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SurfaceDark
                )
            )

            // Приватні нотатки (ТІЛЬКИ ДЛЯ КОУЧА)
            if (isCoach) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Приватні нотатки (Клієнт їх не бачить)", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = privateNotesText,
                    onValueChange = { privateNotesText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Нотатки для себе...", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = SurfaceDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка збереження нотаток
            Button(
                onClick = { onSaveNotes(notesText, privateNotesText) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Зберегти нотатки", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // КНОПКИ КЕРУВАННЯ СТАТУСОМ
            if (!isCoach && (session.status == "pending" || session.status == "confirmed")) {
                // Клієнт може скасувати
                Button(
                    onClick = { onStatusChange("canceled") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Скасувати сесію", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else if (isCoach && session.status == "confirmed") {
                // Коуч може завершити
                Button(
                    onClick = { onStatusChange("completed") },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Завершити сесію", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}