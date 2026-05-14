package com.lazor.growthspace.ui.session

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.data.model.SessionBooking
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    viewModel: SessionsViewModel = koinViewModel(),
    onSessionClick: (String) -> Unit // ДОДАНО: Колбек для кліку по сесії
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 - Майбутні, 1 - Минулі

    val state by viewModel.state.collectAsState()

    val upcomingSessions = state.sessions.filter { it.status in listOf("pending", "confirmed", "available") }
    val pastSessions = state.sessions.filter { it.status in listOf("completed", "cancelled", "canceled") } // Додав canceled на всяк випадок

    val isCoach = state.currentUser?.role == "coach"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Мої сесії", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Search */ },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(SurfaceDarkElevated, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Пошук", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedTabSwitch(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                Crossfade(targetState = selectedTab, label = "ListTransition") { tab ->
                    if (tab == 0) {
                        if (upcomingSessions.isEmpty()) {
                            EmptyStateMessage("У вас немає майбутніх сесій")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(upcomingSessions) { session ->
                                    DynamicSessionCard(
                                        session = session,
                                        isCoach = isCoach,
                                        onConfirm = { viewModel.updateSessionStatus(session.id, "confirmed") },
                                        onCancel = { viewModel.updateSessionStatus(session.id, "canceled") }, // Використовуємо canceled як у БД
                                        onClick = { onSessionClick(session.id) } // ПЕРЕДАЄМО КЛІК
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }
                    } else {
                        if (pastSessions.isEmpty()) {
                            EmptyStateMessage("Історія сесій порожня")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(pastSessions) { session ->
                                    PastSessionCard(
                                        session = session, // Передаємо цілий об'єкт для зручності
                                        isCoach = isCoach,
                                        onClick = { onSessionClick(session.id) } // ПЕРЕДАЄМО КЛІК
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp), contentAlignment = Alignment.Center) {
        Text(message, color = TextGray, fontSize = 16.sp)
    }
}

fun getStatusText(status: String): String = when(status) {
    "available" -> "Вільний слот"
    "pending" -> "Очікує підтвердження"
    "confirmed" -> "Підтверджено"
    "completed" -> "Завершено"
    "cancelled", "canceled" -> "Скасовано"
    else -> status
}

fun getStatusColor(status: String): Color = when(status) {
    "available" -> Color(0xFF00BCD4)
    "pending" -> Color(0xFFFFA000)
    "confirmed" -> Color(0xFF00E676)
    "cancelled", "canceled" -> Color(0xFFFF5252)
    else -> TextGray
}

@Composable
fun DynamicSessionCard(
    session: SessionBooking,
    isCoach: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onClick: () -> Unit // ДОДАНО
) {
    val displayName = if (isCoach) {
        if (session.clientId.isEmpty()) "Вільний слот" else session.clientName
    } else {
        session.coachName
    }

    val specialization = if (isCoach) {
        if (session.clientId.isEmpty()) "Ніхто ще не записався" else "Клієнт"
    } else {
        "Ваш коуч"
    }

    val statusColor = getStatusColor(session.status)
    val statusText = getStatusText(session.status)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)) // Спочатку кліпаємо форму
            .clickable { onClick() }         // Потім додаємо клік (щоб ефект натискання був у формі)
            .border(1.dp, SurfaceDarkElevated, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Хедер
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row {
                Box(modifier = Modifier.size(48.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                    val initial = if (displayName.isNotEmpty() && displayName != "Вільний слот") displayName.take(1) else "?"
                    Text(initial, color = TextWhite, fontWeight = FontWeight.Bold)
                    if (session.status == "confirmed") {
                        Box(modifier = Modifier.size(12.dp).background(Color(0xFF00E676), CircleShape).border(2.dp, BackgroundDark, CircleShape).align(Alignment.BottomEnd))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(displayName, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(specialization, color = TextGray, fontSize = 14.sp)
                }
            }
            Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Дати і Час
        Row(
            modifier = Modifier.fillMaxWidth().background(BackgroundDark, RoundedCornerShape(12.dp)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(session.date, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

            Box(modifier = Modifier.height(16.dp).width(1.dp).background(SurfaceDarkElevated))

            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(session.time, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ДИНАМІЧНІ КНОПКИ ЗАЛЕЖНО ВІД СТАТУСУ ТА РОЛІ
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            if (isCoach && session.status == "pending") {
                // Коуч: Запит від клієнта
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Відхилити", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Підтвердити", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            } else if (session.status == "confirmed") {
                // Підтверджена сесія для обох
                OutlinedButton(
                    onClick = { /* TODO: Відкрити чат напряму */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceDarkElevated),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                ) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Чат", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onClick, // Відкриває деталі
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Outlined.Videocam, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Деталі", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            } else if (isCoach && session.status == "available") {
                // Коуч бачить свій вільний слот
                OutlinedButton(
                    onClick = onCancel, // Видалення слоту
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceDarkElevated),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray)
                ) {
                    Text("Видалити слот", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PastSessionCard(
    session: SessionBooking, // ТЕПЕР ПРИЙМАЄ ЦІЛУ СЕСІЮ
    isCoach: Boolean,
    onClick: () -> Unit // ДОДАНО
) {
    val name = if (isCoach) session.clientName else session.coachName
    val specialization = if (isCoach) "Клієнт" else "Коуч"
    val statusText = getStatusText(session.status)
    val isCancelled = session.status == "cancelled" || session.status == "canceled"
    val statusBgColor = if (isCancelled) Color(0xFF3E1A1A) else SurfaceDarkElevated
    val statusTextColor = if (isCancelled) Color(0xFFFF5252) else TextGray
    val showRepeatButton = session.status == "completed" && !isCoach

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)) // Спочатку кліпаємо форму
            .clickable { onClick() }         // Потім додаємо клік
            .border(1.dp, SurfaceDarkElevated, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row {
                Box(modifier = Modifier.size(48.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                    val initial = if (name.isNotEmpty()) name.take(1) else "?"
                    Text(initial, color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(specialization, color = TextGray, fontSize = 14.sp)
                }
            }
            Surface(color = statusBgColor, shape = RoundedCornerShape(12.dp)) {
                Text(statusText, color = statusTextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            Text(session.date, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Box(modifier = Modifier.height(16.dp).width(1.dp).background(SurfaceDarkElevated))
            Spacer(modifier = Modifier.weight(1f))
            Text(session.time, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showRepeatButton) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onClick, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, SurfaceDarkElevated)) {
                    Text("Нотатки", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { /* TODO: Повторити сесію (поки не реалізовано) */ }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, SurfaceDarkElevated)) {
                    Text("Повторити", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, SurfaceDarkElevated)) {
                Text("Переглянути деталі", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AnimatedTabSwitch(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(SurfaceDarkElevated, RoundedCornerShape(28.dp))
            .padding(4.dp)
    ) {
        val tabWidth = this.maxWidth / 2

        val indicatorOffset by animateDpAsState(
            targetValue = if (selectedTab == 0) 0.dp else tabWidth,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "indicatorOffset"
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .background(PrimaryBlue, RoundedCornerShape(24.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(0) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Майбутні",
                    color = if (selectedTab == 0) BackgroundDark else TextGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(1) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Минулі",
                    color = if (selectedTab == 1) BackgroundDark else TextGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}