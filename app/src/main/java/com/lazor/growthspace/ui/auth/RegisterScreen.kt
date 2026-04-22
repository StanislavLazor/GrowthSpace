package com.lazor.growthspace.ui.auth

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.components.CustomTextField
import com.lazor.growthspace.ui.components.PrimaryButton
import com.lazor.growthspace.ui.theme.*

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    // Базові стейти
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Додаткові стейти для коуча
    var specialization by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    // Стейт для ролі та умов
    var isClientRole by remember { mutableStateOf(true) }
    var isTermsAccepted by remember { mutableStateOf(false) }

    // Логіка валідації
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    val isEmailError = email.isNotEmpty() && !email.matches(emailRegex)
    val isPasswordError = password.isNotEmpty() && password.length < 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Заголовки
        Text(
            text = "Створити\nакаунт",
            color = TextWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Приєднуйтесь до GrowthSpace та почніть свій шлях розвитку.",
            color = TextGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Перемикач ролей
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(SurfaceDarkElevated, shape = RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val tabWidth = maxWidth / 2

            val indicatorOffset by animateDpAsState(
                targetValue = if (isClientRole) 0.dp else tabWidth,
                animationSpec = tween(durationMillis = 300),
                label = "indicatorOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .fillMaxHeight()
                    .background(SurfaceDark, shape = RoundedCornerShape(10.dp))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            )

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isClientRole = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Клієнт",
                        color = if (isClientRole) PrimaryBlue else TextGray,
                        fontWeight = if (isClientRole) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isClientRole = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Коуч",
                        color = if (!isClientRole) PrimaryBlue else TextGray,
                        fontWeight = if (!isClientRole) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Базові поля вводу (Ім'я та Прізвище)
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Ім'я",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = surname,
            onValueChange = { surname = it },
            label = "Прізвище",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Анімавона поля лише для коуча
        AnimatedVisibility(
            visible = !isClientRole,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column {
                CustomTextField(
                    value = specialization,
                    onValueChange = { specialization = it },
                    label = "Спеціалізація (напр., Бізнес-коуч)",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = "Досвід роботи (років)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Тільки цифри
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Поле Email
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = isEmailError
        )
        if (isEmailError) {
            Text(text = "Невірний формат Email", color = StatusCanceled, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
        } else {
            Text(text = "Ми надішлемо вам лист для підтвердження", color = TextGray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Пароля
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль",
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isPasswordError,
            trailingIcon = {
                val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Показати пароль", tint = TextGray)
                }
            }
        )
        if (isPasswordError) {
            Text(text = "Пароль має містити мінімум 8 символів", color = StatusCanceled, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Чекбокс
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = isTermsAccepted,
                onCheckedChange = { isTermsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue, uncheckedColor = TextGray, checkmarkColor = TextWhite)
            )
            val annotatedText = buildAnnotatedString {
                append("Я погоджуюсь з ")
                withStyle(style = SpanStyle(color = PrimaryBlue)) { append("Умовами користування") }
                append(" та ")
                withStyle(style = SpanStyle(color = PrimaryBlue)) { append("Політикою компанії") }
            }
            Text(text = annotatedText, fontSize = 12.sp, color = TextGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val isBaseValid = name.isNotBlank() && surname.isNotBlank() && email.isNotBlank() && !isEmailError && password.isNotBlank() && !isPasswordError && isTermsAccepted
        // Якщо коуч — + ще два поля, якщо клієнт — коучевські поля ігноруються (вважаються true)
        val isCoachValid = if (!isClientRole) specialization.isNotBlank() && experience.isNotBlank() else true

        val isFormValid = isBaseValid && isCoachValid

        PrimaryButton(
            text = "Зареєструватися",
            isEnabled = isFormValid,
            onClick = onRegisterSuccess
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, top = 24.dp), horizontalArrangement = Arrangement.Center) {
            Text(text = "Вже маєте акаунт? ", color = TextGray)
            Text(text = "Увійти", color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLoginClick() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {}, onLoginClick = {})
}