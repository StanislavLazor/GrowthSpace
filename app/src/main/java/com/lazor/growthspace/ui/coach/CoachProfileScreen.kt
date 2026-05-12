package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.User
import com.lazor.growthspace.ui.components.PrimaryButton
import com.lazor.growthspace.ui.theme.*
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachProfileScreen(
    coachId: String, // ТЕПЕР STRING
    onBackClick: () -> Unit,
    onBookSessionClick: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    var coach by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Завантажуємо дані коуча при відкритті екрана
    LaunchedEffect(coachId) {
        try {
            val doc = firestore.collection("users").document(coachId).get().await()
            coach = doc.toObject(User::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Профіль коуча", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (coach != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Аватарка
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(2.dp, PrimaryBlue, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = coach?.name?.firstOrNull()?.uppercase() ?: "C",
                        color = TextWhite,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = coach?.name ?: "", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "Професійний коуч", color = PrimaryBlue, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // Блок Біо
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Про мене", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (coach?.bio?.isNotBlank() == true) coach!!.bio else "Цей коуч поки не додав опис.",
                            color = TextGray,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Забронювати сесію",
                    onClick = onBookSessionClick
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}