package com.lazor.growthspace.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    // Стейт для полів вводу
    var name by remember { mutableStateOf("Марія Коваленко") }
    var email by remember { mutableStateOf("maria.k@example.com") }
    var bio by remember { mutableStateOf("Працюю над підвищенням продуктивності та балансом між роботою та особистим життям. Шукаю коуча для розвитку лідерських навичок") }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Редагування профілю",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {},
                actions = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .background(SurfaceDarkElevated, CircleShape)
                    ) {
                        Icon(
                            // Використовуємо ChevronLeft, щоб стрілка дивилася вліво
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Назад",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Аватар та кнопки під ним
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, PrimaryBlue, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
            ) {
                // Заглушка для фото
                Text("М", color = TextWhite, fontSize = 48.sp, modifier = Modifier.align(Alignment.Center))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { /* TODO: Змінити фото */ },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Змінити", color = TextWhite)
                }
                Button(
                    onClick = { /* TODO: Видалити фото */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Видалити", color = Color(0xFFE91E63))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Поля вводу
            EditField(label = "Ім'я та Прізвище", value = name, onValueChange = { name = it })
            EditField(label = "Email", value = email, onValueChange = { email = it })

            // Поле "Про себе" з лічильником
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Короткий опис / Цілі", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = bio,
                    onValueChange = { if (it.length <= 500) bio = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(1.dp, SurfaceDarkElevated, RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDarkElevated.copy(alpha = 0.5f),
                        unfocusedContainerColor = SurfaceDarkElevated.copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                Text(
                    text = "${bio.length} / 500",
                    color = TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Кнопка збереження
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Зберегти зміни", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(label, color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceDarkElevated, RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceDarkElevated.copy(alpha = 0.5f),
                unfocusedContainerColor = SurfaceDarkElevated.copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}