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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.data.model.Goal
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    val allGoals by viewModel.goals.collectAsState()

    val activeGoals = allGoals.filter { goal -> goal.tasks.any { !it.isCompleted } || goal.tasks.isEmpty() }
    val completedGoals = allGoals.filter { goal -> goal.tasks.isNotEmpty() && goal.tasks.all { it.isCompleted } }

    val totalTasksCount = allGoals.sumOf { it.tasks.size }
    val completedTasksCount = allGoals.sumOf { goal -> goal.tasks.count { it.isCompleted } }
    val activeTasksCount = totalTasksCount - completedTasksCount

    val globalProgress = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0f
    val globalProgressPercent = (globalProgress * 100).toInt()

    // ПАЛІТРА КОЛЬОРІВ ДЛЯ ЦІЛЕЙ (Без червоного і зеленого)
    val activeGoalPalette = listOf(
        Color(0xFF2196F3),
        Color(0xFFC949E0),
        Color(0xFF00BCD4),
        Color(0xFF3F51B5),
        Color(0xFF673AB7),
        Color(0xFF9C27B0),
        Color(0xFFE91E63),
        Color(0xFFFF4081),
        Color(0xFFFF9800),
        Color(0xFFFF9900),
        Color(0xFFFFC107)
    )

    // Колір успіху для завершених цілей
    val successGreen = Color(0xFF00E676)

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, desc, tasks ->
                viewModel.addGoal(title, desc, tasks)
                showAddGoalDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Мій прогрес", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = { showAddGoalDialog = true },
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

            item { ProgressTabSwitch(selectedTab = selectedTab) { selectedTab = it } }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GlowingCircularProgress(progress = globalProgress, text = "$globalProgressPercent%")
                }
            }

            item { StatsRow(goals = activeGoals.size, tasks = activeTasksCount) }

            item {
                Crossfade(targetState = selectedTab, label = "ProgressTab") { tab ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val goalsToShow = if (tab == 0) activeGoals else completedGoals

                        if (goalsToShow.isEmpty()) {
                            Text(
                                text = if (tab == 0) "Усі цілі завершено! 🎉\nАбо ви ще нічого не додали." else "Ще немає завершених цілей",
                                color = TextGray,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                        } else {
                            goalsToShow.forEach { goal ->

                                val totalTasks = goal.tasks.size
                                val completedTasks = goal.tasks.count { it.isCompleted }

                                // Логіка: якщо все виконано -> зелений, інакше -> колір з палітри
                                val cardColor = if (totalTasks > 0 && completedTasks == totalTasks) {
                                    successGreen
                                } else {
                                    val colorIndex = kotlin.math.abs(goal.id.hashCode()) % activeGoalPalette.size
                                    activeGoalPalette[colorIndex]
                                }

                                InteractiveGoalCard(
                                    goal = goal,
                                    color = cardColor,
                                    onTaskToggle = { taskId, isCompleted ->
                                        viewModel.toggleTask(goal.id, taskId, isCompleted)
                                    },
                                    onDeleteClick = {
                                        viewModel.deleteGoal(goal.id)
                                    }
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
                text = "Загальний\nпрогрес",
                color = TextGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
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
fun StatItem(icon: ImageVector, iconColor: Color, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextGray, fontSize = 12.sp)
    }
}

@Composable
fun InteractiveGoalCard(
    goal: Goal,
    color: Color,
    onTaskToggle: (String, Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    val totalTasks = goal.tasks.size
    val completedTasks = goal.tasks.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(600), label = "lin_progress")

    Column(
        modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if(goal.description.isNotBlank()) {
                    Text(goal.description, color = TextGray, fontSize = 13.sp)
                }
            }

            // КНОПКА ВИДАЛЕННЯ
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp).padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Видалити",
                    tint = Color.Red.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${(animatedProgress * 100).toInt()}%", color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(12.dp))

            // Прогрес-бар лінійний
            Box(modifier = Modifier.weight(1f).height(6.dp), contentAlignment = Alignment.CenterStart) {
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
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Список завдань з БД
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            goal.tasks.forEach { task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTaskToggle(task.id, !task.isCompleted) }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) color else SurfaceDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) TextGray else TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
            }
        }
    }
}