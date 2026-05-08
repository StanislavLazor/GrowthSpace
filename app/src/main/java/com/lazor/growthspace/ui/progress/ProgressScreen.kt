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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*

// Моделі даних для наших цілей та завдань
data class TaskItem(val id: Int, val title: String, val initialCompleted: Boolean)

data class GoalData(
    val title: String,
    val coach: String,
    val color: Color,
    val tasks: SnapshotStateList<TaskItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    // 1. СТВОРЮЄМО СПИСКИ ЗАВДАНЬ
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
    val timeTasks = remember {
        mutableStateListOf(
            TaskItem(6, "Впровадити Pomodoro", true),
            TaskItem(7, "Планування тижня", true)
        )
    }

    // 2. ОБ'ЄДНУЄМО ЇХ В ЄДИНУ БАЗУ ЦІЛЕЙ
    val allGoals = remember {
        listOf(
            GoalData("Лідерські навички", "Олександр Мельник", PrimaryBlue, leadershipTasks),
            GoalData("Публічні виступи", "Марія Коваленко", Color(0xFFFFA000), speakingTasks),
            GoalData("Тайм-менеджмент", "Дмитро Ткаченко", Color(0xFF00E676), timeTasks)
        )
    }

    // 3. ДИНАМІЧНЕ СОРТУВАННЯ (Магія тут!)
    // Активна ціль — це та, де є хоча б ОДНЕ невиконане завдання
    val activeGoals = allGoals.filter { goal -> goal.tasks.any { !it.initialCompleted } }
    // Завершена ціль — це та, де ВСІ завдання виконані
    val completedGoals = allGoals.filter { goal -> goal.tasks.all { it.initialCompleted } }

    // 4. ДИНАМІЧНА СТАТИСТИКА
    val totalTasksCount = allGoals.sumOf { it.tasks.size }
    val completedTasksCount = allGoals.sumOf { goal -> goal.tasks.count { it.initialCompleted } }
    val activeTasksCount = totalTasksCount - completedTasksCount

    val globalProgress = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0f
    val globalProgressPercent = (globalProgress * 100).toInt()

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

            // Плавний перемикач
            item {
                ProgressTabSwitch(selectedTab = selectedTab) { selectedTab = it }
            }

            // Великий круговий прогрес (ВЖЕ ДИНАМІЧНИЙ)
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GlowingCircularProgress(progress = globalProgress, text = "$globalProgressPercent%")
                }
            }

            // Плашка зі статистикою (ВЖЕ СИНХРОНІЗОВАНА З АКТИВНИМИ ЦІЛЯМИ)
            item {
                StatsRow(goals = activeGoals.size, tasks = activeTasksCount)
            }

            // Списки карток
            item {
                Crossfade(targetState = selectedTab, label = "ProgressTab") { tab ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val goalsToShow = if (tab == 0) activeGoals else completedGoals

                        if (goalsToShow.isEmpty()) {
                            // Якщо список порожній (наприклад, всі цілі виконано)
                            Text(
                                text = if (tab == 0) "Усі цілі завершено! 🎉" else "Ще немає завершених цілей",
                                color = TextGray,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                        } else {
                            goalsToShow.forEach { goal ->
                                InteractiveGoalCard(
                                    title = goal.title,
                                    coach = goal.coach,
                                    color = goal.color,
                                    tasks = goal.tasks
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

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
        Box(
            modifier = Modifier
                .size(170.dp)
                .background(brush = Brush.radialGradient(colors = listOf(PrimaryBlue.copy(alpha = 0.4f), Color.Transparent)))
        )

        CircularProgressIndicator(progress = { 1f }, modifier = Modifier.size(140.dp), color = SurfaceDarkElevated, strokeWidth = 10.dp, strokeCap = StrokeCap.Round)
        CircularProgressIndicator(progress = { animatedProgress }, modifier = Modifier.size(140.dp), color = PrimaryBlue, strokeWidth = 10.dp, strokeCap = StrokeCap.Round)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Загальний\nпрогрес", // <-- ВИПРАВЛЕНО ТУТ: Розбито на 2 рядки
                color = TextGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center, // <-- Текст тепер ідеально по центру
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun StatsRow(goals: Int, tasks: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(icon = Icons.Outlined.CheckCircle, iconColor = PrimaryBlue, value = goals.toString(), label = "Цілей")
        StatItem(icon = Icons.Default.FlashOn, iconColor = Color(0xFFFFA000), value = tasks.toString(), label = "Завдань")
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
            // <-- Відсоток тепер береться з анімованого прогресу!
            Text("${(animatedProgress * 100).toInt()}%", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().height(6.dp), contentAlignment = Alignment.CenterStart) {
            Box(modifier = Modifier.fillMaxSize().background(BackgroundDark, CircleShape))
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .padding(horizontal = 2.dp)
                        .background(brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.6f), Color.Transparent)), CircleShape)
                )
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
                            // <-- ЗМІНА СТАНУ ЗАВДАННЯ (оновлює ВЕСЬ екран)
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