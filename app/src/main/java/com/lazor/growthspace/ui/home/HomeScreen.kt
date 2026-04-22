package com.lazor.growthspace.ui.home

import com.lazor.growthspace.data.model.Coach
import com.lazor.growthspace.data.model.dummyCoaches
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*

val categories = listOf("Усі", "Бізнес", "Life-coach", "Психологія", "Кар'єра")

@Composable
fun HomeScreen(navController: androidx.navigation.NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 48.dp, bottom = 80.dp) // Відступи для статус-бару та нижнього меню
    ) {
        // Рядок пошуку
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Пошук коучів, тем...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDarkElevated,
                    unfocusedContainerColor = SurfaceDarkElevated,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Фільтри по категоріям
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryBlue else SurfaceDarkElevated)
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            color = TextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Заголовок "Рекомендовані"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Рекомендовані",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Всі",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* Обробка кліку */ }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Горизонтальний список рекомендованих
        item {
            val recommended = dummyCoaches.filter { it.isRecommended }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recommended) { coach ->
                    Box(modifier = Modifier.clickable {
                        navController.navigate("coach_profile/${coach.id}")
                    }) {
                        RecommendedCoachCard(coach)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Заголовок "Всі спеціалісти"
        item {
            Text(
                text = "Всі спеціалісти",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Вертикальний список всіх спеціалістів
        items(dummyCoaches) { coach ->
            Box(modifier = Modifier.clickable {
                navController.navigate("coach_profile/${coach.id}")
            }) {
                SpecialistCard(coach)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Карточка рекомендованого коуча (Велика)
// Карточка рекомендованого коуча (Збільшена)
@Composable
fun RecommendedCoachCard(coach: Coach) {
    Column(
        modifier = Modifier
            .width(260.dp) // Збільшено ширину
            .background(SurfaceDarkElevated, shape = RoundedCornerShape(24.dp))
            .padding(16.dp) // Збільшено внутрішні відступи
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp) // Збільшено висоту області для фотографії
                .background(SurfaceDark, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Заглушка для фото
            Text(
                text = coach.name.take(1),
                fontSize = 56.sp,
                color = TextGray
            )

            // Рейтинг
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = coach.rating.toString(), color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = coach.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = coach.specialization, color = PrimaryBlue, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Від $${coach.price}/год", color = TextGray, fontSize = 14.sp)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SurfaceDark, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// Карточка спеціаліста у списку (Менша, горизонтальна)
@Composable
fun SpecialistCard(coach: Coach) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDarkElevated, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Фото спеціаліста
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(SurfaceDark, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = coach.name.take(1), fontSize = 24.sp, color = TextGray)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = coach.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = coach.rating.toString(), color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = coach.description, color = TextGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .border(1.dp, SurfaceDark, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = coach.tag, color = TextGray, fontSize = 10.sp)
                }
                Text(text = "$${coach.price}/год", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Створюємо фейковий навігатор спеціально для прев'ю
    val fakeNavController = androidx.navigation.compose.rememberNavController()
    HomeScreen(navController = fakeNavController)
}