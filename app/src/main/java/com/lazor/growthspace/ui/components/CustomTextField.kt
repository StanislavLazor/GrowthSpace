package com.lazor.growthspace.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lazor.growthspace.ui.theme.*

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false, // Наш новий параметр!
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    // Стан для відображення/приховування пароля
    var passwordVisible by remember { mutableStateOf(false) }

    // Визначаємо, як показувати текст (крапочками чи нормально)
    val actualVisualTransformation = if (isPassword && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    // Визначаємо тип клавіатури
    val actualKeyboardOptions = if (isPassword) {
        keyboardOptions.copy(keyboardType = KeyboardType.Password)
    } else {
        keyboardOptions
    }

    // Додаємо іконку ока, якщо це поле для пароля
    val actualTrailingIcon: @Composable (() -> Unit)? = if (isPassword) {
        {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Сховати пароль" else "Показати пароль"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description, tint = TextGray)
            }
        }
    } else {
        trailingIcon // Якщо це не пароль, використовуємо ту іконку, яку передали (якщо вона є)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label, color = TextGray) },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = TextGray) }
        },
        trailingIcon = actualTrailingIcon,
        visualTransformation = actualVisualTransformation,
        keyboardOptions = actualKeyboardOptions,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            // Колір тексту
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            errorTextColor = TextWhite,

            // Колір фону поля
            focusedContainerColor = SurfaceDarkElevated,
            unfocusedContainerColor = SurfaceDarkElevated,
            errorContainerColor = SurfaceDarkElevated,

            // Колір рамки
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = StatusCanceled, // Червона рамка при помилці

            // Колір курсора
            cursorColor = PrimaryBlue,
            errorCursorColor = StatusCanceled // Курсор теж стає червоним для краси
        )
    )
}