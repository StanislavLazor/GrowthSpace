package com.lazor.growthspace.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.model.dummyCoaches
import com.lazor.growthspace.ui.components.UserAvatar // ДОДАНО ІМПОРТ
import com.lazor.growthspace.ui.theme.*
import com.lazor.growthspace.ui.session.SessionsViewModel
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmScreen(
    coachId: String,
    selectedDate: String,
    selectedTime: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: SessionsViewModel = koinViewModel()
) {
    var coachName by remember { mutableStateOf("Завантаження...") }
    var coachSpec by remember { mutableStateOf("") }
    var coachPrice by remember { mutableStateOf(1250) }
    var coachPhotoUrl by remember { mutableStateOf("") } // НОВИЙ СТЕЙТ

    // Завантажуємо реальні дані перед збереженням
    LaunchedEffect(coachId) {
        try {
            val db = FirebaseFirestore.getInstance()
            val doc = db.collection("users").document(coachId).get().await()
            if (doc.exists()) {
                coachName = doc.getString("name") ?: "Коуч"
                coachSpec = doc.getString("specialization") ?: "Спеціаліст"
                coachPrice = (doc.getString("price")?.toIntOrNull() ?: 50) * 25
                coachPhotoUrl = doc.getString("photoUrl") ?: "" // ВИТЯГУЄМО ФОТО
            } else {
                val d = dummyCoaches.find { it.id.toString() == coachId } ?: dummyCoaches.first()
                coachName = d.name
                coachSpec = d.specialization
                coachPrice = d.price * 25
                coachPhotoUrl = d.photoUrl // ВИТЯГУЄМО ФОТО
            }
        } catch (e: Exception) {}
    }

    var note by remember { mutableStateOf("") }
    val maxChar = 200
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Підтвердження", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)) {
            // Картка коуча
            Row(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {

                // КОМБІНАЦІЯ АВАТАРА ТА ЗЕЛЕНОЇ КРАПКИ
                Box(modifier = Modifier.size(60.dp)) {
                    UserAvatar(
                        photoUrl = coachPhotoUrl,
                        name = coachName,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.size(14.dp).background(Color(0xFF00E676), CircleShape).border(2.dp, SurfaceDarkElevated, CircleShape).align(Alignment.BottomEnd))
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(coachName, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(coachSpec, color = TextGray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(Modifier.weight(1f), Icons.Default.CalendarToday, "ДАТА", selectedDate)
                    InfoItem(Modifier.weight(1f), Icons.Default.AccessTime, "ЧАС", selectedTime)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(Modifier.weight(1f), Icons.Default.Timelapse, "ТРИВАЛІСТЬ", "60 хв")
                    InfoItem(Modifier.weight(1f), Icons.Default.Payments, "ЦІНА", "$coachPrice ₴")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Нотатка для коуча (опційно)", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= maxChar) note = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Опишіть коротко ваш запит...", color = TextGray.copy(alpha = 0.5f), fontSize = 14.sp) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, unfocusedContainerColor = SurfaceDarkElevated, focusedContainerColor = SurfaceDarkElevated, unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isLoading = true
                    viewModel.requestSessionAsClient(
                        coachId = coachId,
                        coachName = coachName,
                        date = selectedDate,
                        time = selectedTime,
                        onComplete = {
                            isLoading = false
                            onSuccess()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(28.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BackgroundDark, strokeWidth = 2.dp)
                } else {
                    Text("Надіслати запит • $coachPrice ₴", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InfoItem(modifier: Modifier, icon: ImageVector, label: String, value: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}