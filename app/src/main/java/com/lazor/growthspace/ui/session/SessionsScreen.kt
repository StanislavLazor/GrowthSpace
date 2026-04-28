package com.lazor.growthspace.ui.session

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 - Майбутні, 1 - Минулі

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

            // Плавний перемикач (Слайдер)
            AnimatedTabSwitch(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Перемикання списків з анімацією розчинення
            Crossfade(targetState = selectedTab, label = "ListTransition") { tab ->
                if (tab == 0) {
                    UpcomingSessionsList()
                } else {
                    PastSessionsList()
                }
            }
        }
    }
}

// ==========================================
// КОМПОНЕНТ ПЛАВНОГО ПЕРЕМИКАЧА (ТАБІВ)
// ==========================================
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

        // Синя плашка
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .background(PrimaryBlue, RoundedCornerShape(24.dp))
        )

        // Тексти кнопок
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

// ==========================================
// СПИСКИ СЕСІЙ
// ==========================================
@Composable
fun UpcomingSessionsList() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            UpcomingSessionCard(
                name = "Олександр Мельник",
                specialization = "Кар'єрний коуч",
                date = "Завтра, 15 Травня",
                time = "14:00",
                statusText = "Очікується",
                statusColor = Color(0xFFFFA000),
                primaryActionText = "Деталі",
                secondaryActionText = "Чат",
                secondaryActionIcon = Icons.Outlined.ChatBubbleOutline,
                isPrimaryFilled = false
            )
        }
        item {
            UpcomingSessionCard(
                name = "Марія Коваленко",
                specialization = "Лайф-коуч",
                date = "18 Травня",
                time = "10:00",
                statusText = "Підтверджено",
                statusColor = Color(0xFF00E676),
                primaryActionText = "Приєднатись",
                primaryActionIcon = Icons.Outlined.Videocam,
                secondaryActionText = "Деталі",
                isPrimaryFilled = true
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) } // Відступ для нижнього меню
    }
}

@Composable
fun PastSessionsList() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            PastSessionCard(
                name = "Дмитро Ткаченко",
                specialization = "Бізнес-коуч",
                date = "10 Травня",
                time = "16:00",
                statusText = "Завершено",
                statusBgColor = SurfaceDarkElevated,
                statusTextColor = TextGray
            )
        }
        item {
            PastSessionCard(
                name = "Анна Бойко",
                specialization = "Фінансовий коуч",
                date = "5 Травня",
                time = "11:00",
                statusText = "Скасовано",
                statusBgColor = Color(0xFF3E1A1A), // Темно-червоний фон
                statusTextColor = Color(0xFFFF5252), // Яскраво-червоний текст
                showRepeatButton = false
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ==========================================
// КАРТКИ ДЛЯ СПИСКІВ
// ==========================================
@Composable
fun UpcomingSessionCard(
    name: String, specialization: String, date: String, time: String,
    statusText: String, statusColor: Color,
    primaryActionText: String, primaryActionIcon: ImageVector? = null, isPrimaryFilled: Boolean,
    secondaryActionText: String, secondaryActionIcon: ImageVector? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceDarkElevated, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Хедер картки (Коуч + Статус)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row {
                Box(modifier = Modifier.size(48.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                    Text(name.take(1), color = TextWhite, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF00E676), CircleShape).border(2.dp, BackgroundDark, CircleShape).align(Alignment.BottomEnd))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(specialization, color = TextGray, fontSize = 14.sp)
                }
            }
            // Badge статусу
            Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Блок Дати та Часу
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundDark, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(date, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

            Box(modifier = Modifier.height(16.dp).width(1.dp).background(SurfaceDarkElevated))

            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp)) // Заміни на іконку годинника, якщо є
            Spacer(modifier = Modifier.width(8.dp))
            Text(time, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Вторинна кнопка (Чат або Деталі)
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceDarkElevated),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
            ) {
                if (secondaryActionIcon != null) {
                    Icon(secondaryActionIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(secondaryActionText, fontWeight = FontWeight.Bold)
            }

            // Первинна кнопка (Деталі або Приєднатись)
            Button(
                onClick = { },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isPrimaryFilled) PrimaryBlue else Color.Transparent),
                border = if (!isPrimaryFilled) BorderStroke(1.dp, SurfaceDarkElevated) else null
            ) {
                if (primaryActionIcon != null) {
                    Icon(primaryActionIcon, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(primaryActionText, color = if (isPrimaryFilled) BackgroundDark else TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PastSessionCard(
    name: String, specialization: String, date: String, time: String,
    statusText: String, statusBgColor: Color, statusTextColor: Color,
    showRepeatButton: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceDarkElevated, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row {
                Box(modifier = Modifier.size(48.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                    Text(name.take(1), color = TextWhite, fontWeight = FontWeight.Bold)
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
            Text(date, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Box(modifier = Modifier.height(16.dp).width(1.dp).background(SurfaceDarkElevated))
            Spacer(modifier = Modifier.weight(1f))
            Text(time, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showRepeatButton) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, SurfaceDarkElevated)) {
                    Text("Нотатки", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, SurfaceDarkElevated)) {
                    Text("Повторити", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, SurfaceDarkElevated)) {
                Text("Переглянути деталі", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}