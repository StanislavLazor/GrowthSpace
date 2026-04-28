package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingTimeScreen(
    coachId: Int,
    selectedDate: String,
    onBackClick: () -> Unit,
    onChangeDateClick: () -> Unit,
    onConfirmClick: (String) -> Unit
) {
    var selectedDuration by remember { mutableIntStateOf(60) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    // Форматуємо дату для красивого відображення
    val displayDate = formatToDisplayDate(selectedDate)

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Вибір часу", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(BackgroundDark).padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = { selectedTime?.let { onConfirmClick(it) } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = SurfaceDark
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = selectedTime != null // Активна тільки якщо вибрано час
                ) {
                    Text(text = "Підтвердити", color = if (selectedTime != null) TextWhite else TextGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Картка обраної дати
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDarkElevated, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Обрана дата", color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(displayDate, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text("Змінити", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onChangeDateClick() })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Тривалість сесії
            Text("Тривалість сесії", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(30, 60, 90).forEach { duration ->
                    DurationChip(
                        text = "$duration хв",
                        isSelected = selectedDuration == duration,
                        onClick = { selectedDuration = duration }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Доступний час", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))

            // РАНОК
            TimeSectionRow(icon = Icons.Default.LightMode, title = "Ранок")
            TimeGrid(
                slots = listOf(
                    TimeSlot("09:00", true), TimeSlot("09:30", true), TimeSlot("10:00", false),
                    TimeSlot("10:30", false), TimeSlot("11:00", true), TimeSlot("11:30", true)
                ),
                selectedTime = selectedTime,
                onTimeSelect = { selectedTime = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ДЕНЬ
            TimeSectionRow(icon = Icons.Default.WbSunny, title = "День")
            TimeGrid(
                slots = listOf(
                    TimeSlot("12:00", false), TimeSlot("12:30", true), TimeSlot("13:00", true),
                    TimeSlot("13:30", true), TimeSlot("14:00", true), TimeSlot("14:30", true),
                    TimeSlot("15:00", true), TimeSlot("15:30", false), TimeSlot("16:00", true)
                ),
                selectedTime = selectedTime,
                onTimeSelect = { selectedTime = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ВЕЧІР
            TimeSectionRow(icon = Icons.Default.Nightlight, title = "Вечір")
            TimeGrid(
                slots = listOf(
                    TimeSlot("17:00", true), TimeSlot("17:30", true), TimeSlot("18:00", true),
                    TimeSlot("18:30", false), TimeSlot("19:00", true)
                ),
                selectedTime = selectedTime,
                onTimeSelect = { selectedTime = it }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Допоміжні компоненти

@Composable
fun DurationChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
            .border(1.dp, if (isSelected) PrimaryBlue else SurfaceDark, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) PrimaryBlue else TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimeSectionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = TextGray, fontSize = 14.sp)
    }
}

@Composable
fun TimeGrid(slots: List<TimeSlot>, selectedTime: String?, onTimeSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        slots.chunked(3).forEach { rowSlots ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowSlots.forEach { slot ->
                    TimeChip(
                        time = slot.time,
                        isAvailable = slot.isAvailable,
                        isSelected = selectedTime == slot.time,
                        onClick = { onTimeSelect(slot.time) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Заповнюємо порожні місця, якщо в рядку менше 3 елементів
                repeat(3 - rowSlots.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TimeChip(time: String, isAvailable: Boolean, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryBlue else SurfaceDarkElevated)
            .clickable(enabled = isAvailable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = if (isSelected) BackgroundDark else if (isAvailable) TextWhite else TextGray.copy(alpha = 0.3f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = if (!isAvailable) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

data class TimeSlot(val time: String, val isAvailable: Boolean)

fun formatToDisplayDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        val months = arrayOf("Січня", "Лютого", "Березня", "Квітня", "Травня", "Червня", "Липня", "Серпня", "Вересня", "Жовтня", "Листопада", "Грудня")
        val days = arrayOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "НД")
        "${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year}, ${days[date.dayOfWeek.value - 1]}"
    } catch (e: Exception) {
        dateStr
    }
}