package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun CoachProfileScreen(
    coachId: String,
    onBackClick: () -> Unit,
    onBookSessionClick: () -> Unit,
    viewModel: CoachViewModel = koinViewModel()
) {
    val coach by viewModel.coach.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Завантажуємо дані через ViewModel
    LaunchedEffect(coachId) {
        viewModel.loadCoachData(coachId)
    }

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            // Нижня панель з кнопкою (як на макеті)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BackgroundDark,
                shadowElevation = 12.dp
            ) {
                Button(
                    onClick = onBookSessionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Забронювати сесію", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (coach != null) {
            val user = coach!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. HEADER (Photo & Buttons)
                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    // Заглушка для фото (можна додати Coil Image пізніше)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceDarkElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.firstOrNull()?.toString() ?: "",
                            fontSize = 80.sp,
                            color = TextGray.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Кнопки навігації поверх фото
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                        Row {
                            IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)) {
                                Icon(Icons.Default.Share, "Share", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)) {
                                Icon(Icons.Default.FavoriteBorder, "Fav", tint = Color.White)
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Ім'я та Спеціалізація
                    Text(text = user.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text(
                        text = user.specialization.ifBlank { "Професійний коуч" },
                        fontSize = 16.sp,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. STATS ROW
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CoachStatItem("120+", "Сесій")
                        VerticalDivider(color = Color.White.copy(0.1f), modifier = Modifier.height(30.dp))
                        CoachStatItem("4.9 ★", "Рейтинг")
                        VerticalDivider(color = Color.White.copy(0.1f), modifier = Modifier.height(30.dp))
                        CoachStatItem(user.experience.ifBlank { "0" }, "Досвід")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. TABS (UI заглушка як на фото)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Про себе", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(PrimaryBlue))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("Відгуки (43)", color = TextGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray.copy(0.2f)))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. ПРО МЕНЕ
                    Text(
                        text = "Про мене",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user.bio.ifBlank { "Цей коуч ще не заповнив інформацію про себе." },
                        color = TextGray,
                        lineHeight = 22.sp,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 5. СПЕЦІАЛІЗАЦІЯ (Перелік)
                    Text(
                        text = "Спеціалізація",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    user.specialization.split(",").forEach { item ->
                        if (item.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = PrimaryBlue.copy(0.7f), modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(item.trim(), color = TextWhite, fontSize = 15.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 6. PRICE CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, tint = TextGray, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Тривалість сесії", color = TextGray, fontSize = 13.sp)
                                Text("60 хвилин", color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Вартість", color = TextGray, fontSize = 13.sp)
                                Text(
                                    text = if (user.price.contains("грн") || user.price.contains("$")) user.price else "${user.price} грн",
                                    color = PrimaryBlue,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun CoachStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        Text(label, fontSize = 12.sp, color = TextGray)
    }
}