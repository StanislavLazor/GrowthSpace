package com.lazor.growthspace.ui.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.components.CustomTextField
import com.lazor.growthspace.ui.components.PrimaryButton
import com.lazor.growthspace.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    // Стейт для полів
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Логіка валідації Email
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    val isEmailError = email.isNotEmpty() && !email.matches(emailRegex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Заголовки
        Text(
            text = "З поверненням!",
            color = TextWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Увійдіть до свого акаунту, щоб продовжити роботу.",
            color = TextGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Поле Email
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = isEmailError
        )
        if (isEmailError) {
            Text(
                text = "Невірний формат Email",
                color = StatusCanceled,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Пароль
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль",
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Показати пароль", tint = TextGray)
                }
            }
        )

        // Посилання "Забули пароль?"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Забули пароль?",
                color = PrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Головна кнопка
        val isFormValid = email.isNotBlank() && !isEmailError && password.isNotBlank()

        PrimaryButton(
            text = "Увійти",
            isEnabled = isFormValid,
            onClick = onLoginSuccess
        )

        Spacer(modifier = Modifier.weight(1f))

        // Посилання на реєстрацію
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Немає акаунту? ", color = TextGray)
            Text(
                text = "Зареєструватися",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onNavigateToRegister = {},
        onNavigateToForgotPassword = {}
    )
}