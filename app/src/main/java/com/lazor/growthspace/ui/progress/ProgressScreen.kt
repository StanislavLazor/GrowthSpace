package com.lazor.growthspace.ui.progress

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*

data class TaskItem(val id: Int, val title: String, val initialCompleted: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Мій прогрес", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Add goal */ },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(PrimaryBlue, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Додати", tint = BackgroundDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1. Плавний перемикач
            item {
                ProgressTabSwitch(selectedTab = selectedTab) { selectedTab = it }
            }

            // 2. Великий круговий прогрес
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GlowingCircularProgress(progress = 0.74f, text = "74%")
                }
            }

            // 3. Плашка зі статистикою
            item {
                StatsRow()
            }

            // 4. Списки карток
            item {
                Crossfade(targetState = selectedTab, label = "ProgressTab") { tab ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (tab == 0) {
                            val leadershipTasks = remember {
                                mutableStateListOf(
                                    TaskItem(1, "Прочитати книгу з менеджменту", true),
                                    TaskItem(2, "Провести 1-on-1 з командою", false),
                                    TaskItem(3, "Підготувати презентацію стратегії", false)
                                )
                            }
                            val speakingTasks = remember {
                                mutableStateListOf(
                                    TaskItem(4, "Записати відео-пітч", true),
                                    TaskItem(5, "Виступити на мітапі", false)
                                )
                            }

                            InteractiveGoalCard(
                                title = "Лідерські навички",
                                coach = "Олександр Мельник",
                                color = PrimaryBlue,
                                tasks = leadershipTasks
                            )
                            InteractiveGoalCard(
                                title = "Публічні виступи",
                                coach = "Марія Коваленко",
                                color = Color(0xFFFFA000),
                                tasks = speakingTasks
                            )
                        } else {
                            // ЗАВЕРШЕНІ
                            val timeTasks = remember {
                                mutableStateListOf(
                                    TaskItem(6, "Впровадити Pomodoro", true),
                                    TaskItem(7, "Планування тижня", true)
                                )
                            }
                            InteractiveGoalCard(
                                title = "Тайм-менеджмент",
                                coach = "Дмитро Ткаченко",
                                color = Color(0xFF00E676),
                                tasks = timeTasks
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ==========================================
// КОМПОНЕНТИ З ФІКСАМИ
// ==========================================

@Composable
fun ProgressTabSwitch(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().height(56.dp).background(SurfaceDarkElevated, RoundedCornerShape(28.dp)).padding(4.dp)
    ) {
        val tabWidth = this.maxWidth / 2
        val indicatorOffset by animateDpAsState(
            targetValue = if (selectedTab == 0) 0.dp else tabWidth,
            animationSpec = spring(stiffness = Spring.StiffnessLow), label = "offset"
        )
        Box(modifier = Modifier.offset(x = indicatorOffset).width(tabWidth).fillMaxHeight().background(PrimaryBlue, RoundedCornerShape(24.dp)))
        Row(modifier = Modifier.fillMaxSize()) {
            listOf("Активні", "Завершені").forEachIndexed { index, text ->
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text, color = if (selectedTab == index) BackgroundDark else TextGray, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun GlowingCircularProgress(progress: Float, text: String) {
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(1500), label = "circ_progress")

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp).padding(16.dp)) {
        // Ефект світіння
        Box(
            modifier = Modifier
                .size(170.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Фонове коло
        CircularProgressIndicator(progress = { 1f }, modifier = Modifier.size(140.dp), color = SurfaceDarkElevated, strokeWidth = 10.dp, strokeCap = StrokeCap.Round)
        // Активний прогрес
        CircularProgressIndicator(progress = { animatedProgress }, modifier = Modifier.size(140.dp), color = PrimaryBlue, strokeWidth = 10.dp, strokeCap = StrokeCap.Round)

        // Текст у центрі
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text("Загальний прогрес", color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(icon = Icons.Outlined.CheckCircle, iconColor = PrimaryBlue, value = "12", label = "Цілей")
        StatItem(icon = Icons.Default.FlashOn, iconColor = Color(0xFFFFA000), value = "45", label = "Завдань")
        StatItem(icon = Icons.Default.AccessTime, iconColor = Color(0xFF00E676), value = "32h", label = "Часу")
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextGray, fontSize = 12.sp)
    }
}

@Composable
fun InteractiveGoalCard(title: String, coach: String, color: Color, tasks: SnapshotStateList<TaskItem>) {

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.initialCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(600), label = "lin_progress")

    Column(
        modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(title, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Коуч: $coach", color = TextGray, fontSize = 13.sp)
            }
            // Динамічний текст відсотків
            Text("${(progress * 100).toInt()}%", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Лінійний прогрес бар
        Box(modifier = Modifier.fillMaxWidth().height(6.dp), contentAlignment = Alignment.CenterStart) {
            Box(modifier = Modifier.fillMaxSize().background(BackgroundDark, CircleShape))
            if (progress > 0f) {
                // М'яке неонове світіння під лінією
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .padding(horizontal = 2.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(color.copy(alpha = 0.6f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                // Сама активна лінія
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).background(color, CircleShape))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tasks.forEachIndexed { index, task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            tasks[index] = task.copy(initialCompleted = !task.initialCompleted)
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (task.initialCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.initialCompleted) color else SurfaceDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = task.title,
                        color = if (task.initialCompleted) TextGray else TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.initialCompleted) TextDecoration.LineThrough else TextDecoration.None

                    )
                }
            }
        }
    }
}