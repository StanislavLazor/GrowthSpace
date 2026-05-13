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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.session.SessionsViewModel
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScheduleScreen(
    onBackClick: () -> Unit,
    viewModel: SessionsViewModel = koinViewModel()
) {
    val today = LocalDate.now().withYear(2026)
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val allTimeSlots = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00", "19:30", "20:00"
    )

    var selectedSlots by remember(selectedDate) { mutableStateOf(setOf<String>()) }
    var isSaving by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()
    val coachId = state.currentUser?.id

    LaunchedEffect(coachId) {
        if (coachId != null) {
            viewModel.loadAvailableSlotsForCoach(coachId)
        }
    }

    val existingSlots by viewModel.coachAvailableSlots.collectAsState()

    LaunchedEffect(selectedDate, existingSlots) {
        val dateStr = selectedDate.toString()
        selectedSlots = existingSlots.filter { it.date == dateStr }.map { it.time }.toSet()
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Мій розклад", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(BackgroundDark).padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = {
                        isSaving = true
                        selectedSlots.forEach { time ->
                            if (existingSlots.none { it.date == selectedDate.toString() && it.time == time }) {
                                viewModel.createAvailableSlot(selectedDate.toString(), time)
                            }
                        }
                        isSaving = false
                        // Опціонально: можна додати onBackClick() сюди, щоб екран закривався після збереження
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(28.dp),
                    enabled = selectedSlots.isNotEmpty() && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, tint = BackgroundDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Зберегти розклад", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
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
            Text("Оберіть дату та додайте вільні години, коли ви готові приймати клієнтів.", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))

            // МІНІ-КАЛЕНДАР
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(16.dp)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = TextWhite)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(getUkrainianMonthName(selectedDate.monthValue), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(selectedDate.dayOfMonth.toString(), color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(getUkrainianDayOfWeek(selectedDate.dayOfWeek.value), color = TextGray, fontSize = 12.sp)
                }

                IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextWhite)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Робочі години", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            // СТАБІЛЬНА СІТКА ГОДИН ЗАМІСТЬ FlowRow
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                allTimeSlots.chunked(3).forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSlots.forEach { time ->
                            val isSelected = selectedSlots.contains(time)
                            val isAlreadyBooked = existingSlots.any { it.date == selectedDate.toString() && it.time == time && it.status != "available" }

                            CoachTimeChip(
                                time = time,
                                isSelected = isSelected,
                                isBooked = isAlreadyBooked,
                                modifier = Modifier.weight(1f), // Розтягуємо рівномірно
                                onClick = {
                                    if (!isAlreadyBooked) {
                                        selectedSlots = if (isSelected) {
                                            selectedSlots - time
                                        } else {
                                            selectedSlots + time
                                        }
                                    }
                                }
                            )
                        }
                        // Добиваємо порожніми спейсерами, якщо в рядку менше 3 елементів
                        repeat(3 - rowSlots.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun CoachTimeChip(time: String, isSelected: Boolean, isBooked: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isBooked -> Color(0xFFFFA000).copy(alpha = 0.2f) // Жовтий, якщо заброньовано
                    isSelected -> PrimaryBlue // Синій, якщо додано
                    else -> SurfaceDarkElevated // Сірий, якщо вимкнено
                }
            )
            .border(
                1.dp,
                if (isBooked) Color(0xFFFFA000) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isBooked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = if (isSelected || isBooked) TextWhite else TextGray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getUkrainianDayOfWeek(day: Int): String {
    val days = arrayOf("Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота", "Неділя")
    return days[day - 1]
}
