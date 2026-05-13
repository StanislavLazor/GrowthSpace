package com.lazor.growthspace.ui.coach

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.data.model.dummyCoaches
import com.lazor.growthspace.ui.theme.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingStatusScreen(
    coachId: String,
    date: String,
    time: String,
    onGoToSessions: () -> Unit
) {
    val coach = dummyCoaches.find { it.id.toString() == coachId } ?: dummyCoaches.first()

    // Парсимо дату для відображення числа та місяця
    val localDate = try { LocalDate.parse(date) } catch(e: Exception) { LocalDate.now() }
    val day = localDate.dayOfMonth.toString()
    val monthShort = getShortMonthName(localDate.monthValue)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Статус", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // ГІГАНТСЬКА ІКОНКА ЗІ СВІТІННЯМ
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFA000).copy(alpha = 0.25f), // Центр світіння
                                    Color.Transparent // Розчинення по краях
                                )
                            )
                        )
                )
                // Основне коло
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(BackgroundDark, CircleShape) // Темний фон як на макеті
                        .border(1.dp, Color(0xFFFFA000).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status Badge
            Surface(
                color = Color(0xFFFFA000).copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.border(1.dp, Color(0xFFFFA000).copy(alpha = 0.4f), CircleShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFFFA000), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Очікується", color = Color(0xFFFFA000), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Запит надіслано",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${coach.name} отримав ваш запит. Ми повідомимо вас, як тільки він підтвердить сесію.",
                color = TextGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // КАРТКА ДЕТАЛЕЙ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Блок дати
                Column(
                    modifier = Modifier
                        .background(BackgroundDark, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(monthShort, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(day, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Блок часу та типу
                Column {
                    Text("$time - ${calculateEndTime(time)}", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Онлайн відеодзвінок", color = TextGray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // КНОПКА ПЕРЕХОДУ
            Button(
                onClick = onGoToSessions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkElevated),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceDark)
            ) {
                Text("Перейти до сесій", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Допоміжні функції
fun getShortMonthName(month: Int): String {
    val months = arrayOf("СІЧ", "ЛЮТ", "БЕР", "КВІ", "ТРАВ", "ЧЕР", "ЛИП", "СЕР", "ВЕР", "ЖОВ", "ЛИС", "ГРУ")
    return months[month - 1]
}

@SuppressLint("DefaultLocale")
fun calculateEndTime(startTime: String): String {
    return try {
        val parts = startTime.split(":")
        val hour = parts[0].toInt()
        val nextHour = if (hour + 1 >= 24) 0 else hour + 1
        String.format("%02d:%s", nextHour, parts[1])
    } catch (e: Exception) { "" }
}