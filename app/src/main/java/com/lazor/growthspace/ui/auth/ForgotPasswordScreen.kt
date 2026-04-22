package com.lazor.growthspace.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.components.CustomTextField
import com.lazor.growthspace.ui.components.PrimaryButton
import com.lazor.growthspace.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onSendLinkClick: (String) -> Unit
) {
    // Стейт для зберігання введеного email
    var email by remember { mutableStateOf("") }

    // Логіка валідації Email
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    val isEmailError = email.isNotEmpty() && !email.matches(emailRegex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        // Центральна картка
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(SurfaceDark, shape = RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: Замінити іконку на щось нормальне
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(width = 2.dp, color = TextWhite, shape = CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Забули пароль?",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Опис інструкції
            Text(
                text = "Введіть вашу пошту для скидання. Ми надішлемо інструкції на вказану адресу.",
                color = TextGray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Поле вводу Email з валідацією
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Електронна пошта",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = isEmailError
            )

            // Відображення тексту помилки
            if (isEmailError) {
                Text(
                    text = "Невірний формат Email",
                    color = StatusCanceled,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp).align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Головна кнопка
            val isFormValid = email.isNotBlank() && !isEmailError

            PrimaryButton(
                text = "Надіслати посилання",
                isEnabled = isFormValid, // Кнопка активна тільки при вірному email
                onClick = { onSendLinkClick(email) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Нижня навігація "Назад до входу" з іконкою
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onBackClick() } // Обробка кліку
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Назад до входу",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        onBackClick = {},
        onSendLinkClick = {}
    )
}