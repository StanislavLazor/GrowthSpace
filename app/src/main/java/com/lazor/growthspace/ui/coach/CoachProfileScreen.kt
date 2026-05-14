package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.data.model.Review
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun CoachProfileScreen(
    coachId: String,
    onBackClick: () -> Unit,
    onBookSessionClick: () -> Unit,
    viewModel: CoachViewModel = koinViewModel()
) {
    val coach by viewModel.coach.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(coachId) {
        viewModel.loadCoachData(coachId)
    }

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            if (coach != null) {
                Surface(modifier = Modifier.fillMaxWidth(), color = BackgroundDark, shadowElevation = 12.dp) {
                    Button(
                        onClick = onBookSessionClick,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Забронювати сесію", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
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
                // HEADER Section
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    Box(modifier = Modifier.fillMaxSize().background(SurfaceDarkElevated), contentAlignment = Alignment.Center) {
                        Text(user.name.firstOrNull()?.toString() ?: "", fontSize = 80.sp, color = TextGray.copy(0.3f), fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        IconButton(onClick = onBackClick, modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text(user.specialization.ifBlank { "Професійний коуч" }, fontSize = 16.sp, color = TextGray)

                    Spacer(modifier = Modifier.height(20.dp))

                    // STATS
                    Row(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        CoachStatItem("120+", "Сесій")

                        val averageRating = if (reviews.isEmpty()) 5.0 else reviews.map { it.rating }.average()
                        val formattedRating = String.format(Locale.US, "%.1f", averageRating)
                        CoachStatItem("$formattedRating ★", "Рейтинг")

                        CoachStatItem(user.experience.ifBlank { "0" }, "Досвід")
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // TABS
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TabItem(title = "Про себе", isSelected = selectedTab == 0, modifier = Modifier.weight(1f)) { selectedTab = 0 }
                        TabItem(title = "Відгуки (${reviews.size})", isSelected = selectedTab == 1, modifier = Modifier.weight(1f)) { selectedTab = 1 }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CONTENT
                    if (selectedTab == 0) {
                        AboutSection(user)
                    } else {
                        ReviewsSection(
                            reviews = reviews,
                            currentUserId = currentUser?.id, // Передаємо ID поточного юзера
                            canAddReview = currentUser?.role == "client" && currentUser?.id != coachId,
                            onAddReviewClick = { showReviewDialog = true },
                            onDeleteReviewClick = { reviewId -> viewModel.deleteReview(reviewId) } // Прокидаємо видалення
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            if (showReviewDialog) {
                AddReviewDialog(
                    onDismiss = { showReviewDialog = false },
                    onSubmit = { selectedRating, enteredText ->
                        viewModel.addReview(coachId, selectedRating, enteredText)
                        showReviewDialog = false
                    }
                )
            }
        }
    }
}

// --- ДОПОМІЖНІ КОМПОНЕНТИ ---

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, text: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDarkElevated,
        title = { Text("Залишити відгук", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color(0xFFFFC107) else SurfaceDark,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { rating = i }
                                .padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Поділіться враженнями...", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryBlue, unfocusedBorderColor = SurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onSubmit(rating, text) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = text.isNotBlank()
            ) { Text("Відправити", color = BackgroundDark, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати", color = TextGray) }
        }
    )
}

@Composable
fun CoachStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        Text(label, fontSize = 12.sp, color = TextGray)
    }
}

@Composable
fun TabItem(title: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.clickable { onClick() }) {
        Text(title, color = if (isSelected) PrimaryBlue else TextGray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(if (isSelected) 2.dp else 1.dp).background(if (isSelected) PrimaryBlue else Color.Gray.copy(0.2f)))
    }
}

@Composable
fun AboutSection(user: com.lazor.growthspace.data.model.User) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Про мене", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        Spacer(modifier = Modifier.height(12.dp))
        Text(user.bio.ifBlank { "Інформація відсутня." }, color = TextGray, lineHeight = 22.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Вартість", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${user.price} грн / 60 хв", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReviewsSection(
    reviews: List<Review>,
    currentUserId: String?,
    canAddReview: Boolean,
    onAddReviewClick: () -> Unit,
    onDeleteReviewClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (canAddReview) {
            Button(
                onClick = onAddReviewClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkElevated)
            ) {
                Icon(Icons.Default.Star, null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Написати відгук", color = TextWhite)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (reviews.isEmpty()) {
            Text("Відгуків поки немає. Станьте першим!", color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(24.dp))
        } else {
            reviews.forEach { review ->
                ReviewItem(
                    review = review,
                    isOwnReview = review.clientId == currentUserId, // Перевіряємо, чи це наш відгук
                    onDeleteClick = { onDeleteReviewClick(review.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: Review,
    isOwnReview: Boolean,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDarkElevated, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.clientName.firstOrNull()?.toString() ?: "",
                    color = BackgroundDark,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) { // Забираємо весь доступний простір
                Text(review.clientName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${review.rating} ★", color = PrimaryBlue, fontSize = 12.sp)
            }

            // Якщо це наш відгук — показуємо іконку видалення
            if (isOwnReview) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Видалити відгук",
                        tint = Color(0xFFFF5252) // Червоний колір смітника
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(review.text, color = TextGray, fontSize = 14.sp)
    }
}