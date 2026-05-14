package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.dummyCoaches
import com.lazor.growthspace.ui.components.UserAvatar // ДОДАНО ІМПОРТ
import com.lazor.growthspace.ui.theme.*
import com.lazor.growthspace.ui.session.SessionsViewModel
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun BookingDateScreen(
    coachId: String,
    onBackClick: () -> Unit,
    onNextClick: (LocalDate) -> Unit,
    viewModel: SessionsViewModel = koinViewModel()
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now().withYear(2026)
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }

    // Стейт для реальних даних коуча
    var coachName by remember { mutableStateOf("Завантаження...") }
    var coachPriceText by remember { mutableStateOf("") }
    var coachPhotoUrl by remember { mutableStateOf("") } // НОВИЙ СТЕЙТ

    // Завантажуємо слоти ТА профіль коуча
    LaunchedEffect(coachId) {
        viewModel.loadAvailableSlotsForCoach(coachId)
        try {
            val db = FirebaseFirestore.getInstance()
            val doc = db.collection("users").document(coachId).get().await()
            if (doc.exists()) {
                coachName = doc.getString("name") ?: "Коуч"
                val p = doc.getString("price")?.toIntOrNull() ?: 50
                coachPriceText = "${p * 25} ₴"
                coachPhotoUrl = doc.getString("photoUrl") ?: "" // ВИТЯГУЄМО ФОТО
            } else {
                // Фолбек для тестових коучів
                val d = dummyCoaches.find { it.id.toString() == coachId } ?: dummyCoaches.first()
                coachName = d.name
                coachPriceText = "${d.price * 25} ₴"
                coachPhotoUrl = d.photoUrl // ВИТЯГУЄМО ФОТО
            }
        } catch (e: Exception) {
            coachName = "Коуч"
        }
    }

    val coachSlots by viewModel.coachAvailableSlots.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 16.dp, start = 20.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(SurfaceDark, CircleShape).clip(CircleShape).clickable { onBackClick() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite, modifier = Modifier.size(20.dp))
                }
                Text("Вибір дати", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(40.dp))
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(BackgroundDark).padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = { selectedDate?.let { onNextClick(it) } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, disabledContainerColor = SurfaceDark),
                    shape = RoundedCornerShape(28.dp),
                    enabled = selectedDate != null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Далі", color = if (selectedDate != null) TextWhite else TextGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = if (selectedDate != null) TextWhite else TextGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)) {
            // Картка Коуча
            Row(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

                // ВСТАВЛЕНО НОВИЙ АВАТАР
                UserAvatar(
                    photoUrl = coachPhotoUrl,
                    name = coachName,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = coachName, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Сесія: 60 хвилин • $coachPriceText", color = TextGray, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Календар
            Column(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    val canGoBack = currentMonth.isAfter(YearMonth.from(today))
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }, enabled = canGoBack) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = if (canGoBack) TextWhite else TextGray)
                    }
                    Text(text = "${getUkrainianMonthName(currentMonth.monthValue)} ${currentMonth.year}", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextWhite)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "НД").forEach { day ->
                        Text(text = day, color = TextGray, fontSize = 12.sp, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                val daysInMonth = currentMonth.lengthOfMonth()
                val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
                var dayCounter = 1
                for (week in 0..5) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceAround) {
                        for (dayOfWeek in 1..7) {
                            if ((week == 0 && dayOfWeek < firstDayOfWeek) || dayCounter > daysInMonth) {
                                Spacer(modifier = Modifier.size(40.dp))
                            } else {
                                val date = currentMonth.atDay(dayCounter)
                                val isPast = date.isBefore(today)
                                val isSelected = selectedDate == date
                                val dateStr = date.toString()
                                val slotsOnDate = coachSlots.count { it.date == dateStr }
                                val hasSlots = !isPast && slotsOnDate > 0
                                val hasManySlots = hasSlots && slotsOnDate >= 3

                                CalendarDayItem(day = dayCounter.toString(), isPast = isPast, hasSlots = hasSlots, hasManySlots = hasManySlots, isSelected = isSelected, onClick = { if (!isPast && hasSlots) selectedDate = date })
                                dayCounter++
                            }
                        }
                    }
                    if (dayCounter > daysInMonth) break
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                LegendItem(color = PrimaryBlue, text = "Багато слотів", isDot = true)
                LegendItem(color = SurfaceDark, text = "Доступно", isRing = true)
                LegendItem(color = SurfaceDark, text = "Немає місць", isCross = true)
            }
        }
    }
}

@Composable
fun CalendarDayItem(day: String, isPast: Boolean, hasSlots: Boolean, hasManySlots: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isSelected) PrimaryBlue else Color.Transparent).border(width = 1.dp, color = if (isSelected) Color.Transparent else if (!isPast && hasSlots) SurfaceDark else Color.Transparent, shape = CircleShape).clickable(enabled = !isPast && hasSlots) { onClick() }, contentAlignment = Alignment.Center) {
        Text(text = day, color = if (isSelected) BackgroundDark else if (isPast || !hasSlots) TextGray.copy(alpha = 0.5f) else TextWhite, fontSize = 16.sp, fontWeight = if (isSelected || (!isPast && hasSlots)) FontWeight.Bold else FontWeight.Normal)
        if (!isSelected && hasManySlots && !isPast) Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp).size(4.dp).background(PrimaryBlue, CircleShape))
    }
}

@Composable
fun LegendItem(color: Color, text: String, isDot: Boolean = false, isRing: Boolean = false, isCross: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(if (isDot) color else Color.Transparent, CircleShape).border(1.dp, if (isRing || isCross) TextGray else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) {
            if (isCross) Text("×", color = TextGray, fontSize = 10.sp, modifier = Modifier.offset(y = (-1).dp))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = TextGray, fontSize = 12.sp)
    }
}

fun getUkrainianMonthName(month: Int): String {
    val months = arrayOf("Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень", "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень")
    return months[month - 1]
}