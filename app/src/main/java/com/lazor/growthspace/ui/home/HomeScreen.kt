package com.lazor.growthspace.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val coaches by viewModel.coaches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState() // Підключено до VM

    val categories = listOf("Усі", "Бізнес", "Life-coach", "Психологія")

    Scaffold(
        containerColor = BackgroundDark,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Пошук
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange, // Працює через VM
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    placeholder = { Text("Пошук за ім'ям або спеціалізацією...", color = TextGray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextGray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDarkElevated,
                        unfocusedContainerColor = SurfaceDarkElevated,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryBlue
                    ),
                    singleLine = true
                )
            }

            // 2. Категорії (Чипси)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            modifier = Modifier.clickable { viewModel.onCategorySelect(category) }, // Працює через VM
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) PrimaryBlue else SurfaceDarkElevated,
                            border = if (!isSelected) BorderStroke(1.dp, Color.White.copy(0.1f)) else null
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) BackgroundDark else TextWhite,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 3. Рекомендовані (Ховаємо, якщо активний пошук)
            if (searchQuery.isBlank() && selectedCategory == "Усі" && !isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Рекомендовані", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Всі", color = PrimaryBlue, fontSize = 14.sp)
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(coaches.take(3)) { coach ->
                            RecommendedCoachCard(coach) {
                                navController.navigate("coach_profile/${coach.id}")
                            }
                        }
                    }
                }
            }

            // 4. Всі спеціалісти (або результати пошуку)
            item {
                val listTitle = if (searchQuery.isNotBlank() || selectedCategory != "Усі") "Результати пошуку" else "Всі спеціалісти"
                Text(
                    text = listTitle,
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            if (isLoading) {
                item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryBlue) } }
            } else if (coaches.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Нічого не знайдено \uD83D\uDD0E", color = TextGray)
                    }
                }
            } else {
                items(coaches) { coach ->
                    CoachListCard(coach) {
                        navController.navigate("coach_profile/${coach.id}")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// --- КОМПОНЕНТИ КАРТОК ---
// (Твої карточки залишилися без змін, просто скопіюй їх)

@Composable
fun RecommendedCoachCard(coach: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceDark)) {
                    Text(coach.name.take(1), Modifier.align(Alignment.Center), color = TextGray, fontSize = 40.sp)
                }
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                    color = Color.Black.copy(0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                        Text(" 5.0", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(coach.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
            Text(coach.specialization, color = PrimaryBlue, fontSize = 13.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Від ${coach.price} ₴", color = TextGray, fontSize = 12.sp)
                Box(Modifier.size(32.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ArrowForward, null, tint = TextWhite, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CoachListCard(coach: User, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDarkElevated
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceDark)) {
                Text(coach.name.take(1), Modifier.align(Alignment.Center), color = TextGray, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(coach.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Text(" 5.0", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Text(coach.specialization, color = TextGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = SurfaceDark, shape = RoundedCornerShape(8.dp)) {
                        Text("Coach", color = TextGray, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(coach.price, color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}