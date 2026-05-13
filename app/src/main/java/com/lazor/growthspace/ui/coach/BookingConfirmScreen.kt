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
import com.lazor.growthspace.data.model.dummyCoaches
import com.lazor.growthspace.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmScreen(
    coachId: String,
    selectedDate: String,
    selectedTime: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    // Знаходимо коуча за ID, щоб підтягнути його ім'я та ціну
    val coach = dummyCoaches.find { it.id.toString() == coachId } ?: dummyCoaches.first()
    val scope = rememberCoroutineScope()

    // Стейт для текстового поля з нотаткою
    var note by remember { mutableStateOf("") }
    val maxChar = 200

    // Стейт для кнопки (крутилка завантаження)
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Підтвердження", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // 1. Картка коуча
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(60.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                    Text(coach.name.take(1), color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    // Зелена крапка статусу "в мережі"
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(0xFF00E676), CircleShape)
                            .border(2.dp, SurfaceDarkElevated, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(coach.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(coach.specialization, color = TextGray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Деталі бронювання (Сітка)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(Modifier.weight(1f), Icons.Default.CalendarToday, "ДАТА", selectedDate)
                    InfoItem(Modifier.weight(1f), Icons.Default.AccessTime, "ЧАС", selectedTime)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(Modifier.weight(1f), Icons.Default.Timelapse, "ТРИВАЛІСТЬ", "60 хв")
                    // Рахуємо ціну (наприклад, 50$ * 25 = 1250 ₴)
                    InfoItem(Modifier.weight(1f), Icons.Default.Payments, "ЦІНА", "${coach.price * 25} ₴")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Поле для нотатки
            Text("Нотатка для коуча (опційно)", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= maxChar) note = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Опишіть коротко ваш запит або цілі на цю сесію...", color = TextGray.copy(alpha = 0.5f), fontSize = 14.sp) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    unfocusedContainerColor = SurfaceDarkElevated,
                    focusedContainerColor = SurfaceDarkElevated,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryBlue
                )
            )
            Text(
                "${note.length}/$maxChar",
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Оплата
            Text("Оплата", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDarkElevated, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Text("•••• 4242", color = TextWhite, modifier = Modifier.weight(1f))
                Text("Змінити", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. Кнопка "Надіслати запит" з анімацією завантаження
            Button(
                onClick = {
                    isLoading = true // Вмикаємо крутилку
                    scope.launch {
                        delay(2000) // Чекаємо 2 секунди (ніби йде запит на сервер)
                        isLoading = false
                        onSuccess() // Виконуємо перехід на головний екран
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(28.dp),
                enabled = !isLoading // Блокуємо кнопку від подвійного кліку
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = BackgroundDark,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Обробка...", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Надіслати запит • ${coach.price * 25} ₴", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Допоміжний компонент для сітки з іконками
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