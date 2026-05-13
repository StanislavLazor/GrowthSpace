package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*
import com.lazor.growthspace.ui.session.SessionsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingTimeScreen(
    coachId: String,
    selectedDate: String,
    onBackClick: () -> Unit,
    onChangeDateClick: () -> Unit,
    onConfirmClick: (String) -> Unit,
    viewModel: SessionsViewModel = koinViewModel()
) {
    var selectedTime by remember { mutableStateOf<String?>(null) }
    val displayDate = formatToDisplayDate(selectedDate)

    // Анімація завантаження, щоб екран не блимав порожнім списком
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(coachId) {
        viewModel.loadAvailableSlotsForCoach(coachId)
        delay(600) // Даємо Firebase частку секунди на отримання даних
        isLoading = false
    }

    val coachSlots by viewModel.coachAvailableSlots.collectAsState()
    val slotsForDate = coachSlots.filter { it.date == selectedDate }.sortedBy { it.time }

    val morningSlots = slotsForDate.filter { it.time < "12:00" }
    val daySlots = slotsForDate.filter { it.time in "12:00".."16:59" }
    val eveningSlots = slotsForDate.filter { it.time >= "17:00" }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Вибір часу", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(BackgroundDark).padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = { selectedTime?.let { onConfirmClick(it) } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, disabledContainerColor = SurfaceDark),
                    shape = RoundedCornerShape(28.dp),
                    enabled = selectedTime != null
                ) {
                    Text("Підтвердити", color = if (selectedTime != null) TextWhite else TextGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {

            Row(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(16.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Обрана дата", color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(displayDate, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text("Змінити", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onChangeDateClick() })
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (slotsForDate.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("На цю дату більше немає вільних слотів.", color = TextGray, fontSize = 16.sp, textAlign = TextAlign.Center)
                }
            } else {
                Text("Доступний час", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

                if (morningSlots.isNotEmpty()) {
                    TimeSectionRow(icon = Icons.Default.LightMode, title = "Ранок")
                    TimeGrid(slots = morningSlots.map { it.time }, selectedTime = selectedTime, onTimeSelect = { selectedTime = it })
                    Spacer(modifier = Modifier.height(24.dp))
                }
                if (daySlots.isNotEmpty()) {
                    TimeSectionRow(icon = Icons.Default.WbSunny, title = "День")
                    TimeGrid(slots = daySlots.map { it.time }, selectedTime = selectedTime, onTimeSelect = { selectedTime = it })
                    Spacer(modifier = Modifier.height(24.dp))
                }
                if (eveningSlots.isNotEmpty()) {
                    TimeSectionRow(icon = Icons.Default.Nightlight, title = "Вечір")
                    TimeGrid(slots = eveningSlots.map { it.time }, selectedTime = selectedTime, onTimeSelect = { selectedTime = it })
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
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
fun TimeGrid(slots: List<String>, selectedTime: String?, onTimeSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        slots.chunked(3).forEach { rowSlots ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowSlots.forEach { time ->
                    TimeChip(time = time, isSelected = selectedTime == time, onClick = { onTimeSelect(time) }, modifier = Modifier.weight(1f))
                }
                repeat(3 - rowSlots.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun TimeChip(time: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(48.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) PrimaryBlue else SurfaceDarkElevated).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(text = time, color = if (isSelected) BackgroundDark else TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

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