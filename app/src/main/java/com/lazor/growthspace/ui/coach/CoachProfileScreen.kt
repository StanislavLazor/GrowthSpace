package com.lazor.growthspace.ui.coach

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.data.model.dummyCoaches
import com.lazor.growthspace.ui.theme.*

@Composable
fun CoachProfileScreen(
    coachId: Int,
    onBackClick: () -> Unit,
    onBookSessionClick: () -> Unit
) {
    val coach = dummyCoaches.find { it.id == coachId } ?: dummyCoaches.first()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Про себе", "Відгуки (${coach.reviews.size})")

    // Стейт для розгортання тексту біографії
    var isBioExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onBookSessionClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Забронювати сесію", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 80.dp, bottom = 16.dp)
            ) {
                // Блок 1: Фото та основна інфо
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(140.dp).background(SurfaceDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = coach.name.take(1), fontSize = 64.sp, color = TextGray)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = coach.name, color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = coach.specialization, color = TextGray, fontSize = 16.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 20.dp)
                        ) {
                            items(coach.tags) { tag ->
                                Box(
                                    modifier = Modifier.background(SurfaceDarkElevated, RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(text = tag, color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Блок 2: Картки статистики
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard(value = "${coach.totalSessions}+", label = "Сесій", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        StatCard(value = coach.rating.toString(), label = "Рейтинг", icon = Icons.Default.Star, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        StatCard(value = coach.yearsExp.toString(), label = "Років досвіду", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Блок 3: Таби
                item {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = BackgroundDark,
                        contentColor = PrimaryBlue,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = PrimaryBlue)
                        },
                        divider = { HorizontalDivider(color = SurfaceDark) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) },
                                selectedContentColor = PrimaryBlue,
                                unselectedContentColor = TextGray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Блок 4: Контент вкладок
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        if (selectedTabIndex == 0) {
                            // КОНТЕНТ: Про себе
                            Text(text = "Про мене", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Текст, який розгортається з анімацією
                            Column(modifier = Modifier.animateContentSize()) {
                                Text(
                                    text = coach.fullBio,
                                    color = TextGray,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    maxLines = if (isBioExpanded) Int.MAX_VALUE else 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isBioExpanded) "Згорнути" else "Читати повністю",
                                    color = PrimaryBlue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable { isBioExpanded = !isBioExpanded } // Обробка кліку
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(text = "Спеціалізація", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))

                            coach.specializationPoints.forEach { point ->
                                ChecklistItem(text = point)
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, shape = RoundedCornerShape(16.dp)).padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = "Тривалість сесії", color = TextGray, fontSize = 12.sp)
                                        Text(text = "60 хвилин", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Вартість", color = TextGray, fontSize = 12.sp)
                                    Text(text = "$${coach.price}", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                        } else {
                            // КОНТЕНТ: Відгуки
                            if (coach.reviews.isEmpty()) {
                                Text(
                                    text = "Поки що немає відгуків...",
                                    color = TextGray,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                // Перебираємо всі відгуки поточного коуча
                                coach.reviews.forEach { review ->
                                    ReviewCard(
                                        name = review.authorName,
                                        date = review.date,
                                        rating = review.rating,
                                        reviewText = review.text
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(80.dp)) // Відступ для нижньої кнопки
                        }
                    }
                }
            }

            HeaderButtons(onBackClick = onBackClick)
        }
    }
}

// НОВИЙ КОМПОНЕНТ: Картка відгуку
@Composable
fun ReviewCard(name: String, date: String, rating: Double, reviewText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDarkElevated, RoundedCornerShape(20.dp)) // Трохи кругліші кути
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(SurfaceDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name.take(1), color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = date, color = TextGray, fontSize = 12.sp)
                }
            }
            // Плашка з рейтингом
            Row(
                modifier = Modifier
                    .border(1.dp, SurfaceDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = rating.toString(), color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = reviewText, color = TextGray, fontSize = 15.sp, lineHeight = 22.sp)
    }
}

// Компонент картки статистики
@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Column(
        modifier = modifier.border(width = 1.dp, color = SurfaceDark, shape = RoundedCornerShape(16.dp)).padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            if (icon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = TextGray, fontSize = 12.sp)
    }
}

@Composable
fun ChecklistItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = TextGray, fontSize = 14.sp)
    }
}

@Composable
fun HeaderButtons(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)
        Row {
            HeaderIconButton(icon = Icons.Default.IosShare, onClick = { })
            Spacer(modifier = Modifier.width(16.dp))
            HeaderIconButton(icon = Icons.Default.FavoriteBorder, onClick = { })
        }
    }
}

@Composable
fun HeaderIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape).clip(CircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CoachProfileScreenPreview() {
    CoachProfileScreen(coachId = 1, onBackClick = {}, onBookSessionClick = {})
}