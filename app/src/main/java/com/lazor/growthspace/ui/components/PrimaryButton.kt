package com.lazor.growthspace.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.*

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false // Додав параметр для крутилки завантаження!
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading, // Блокуємо клік, якщо йде завантаження
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp), // Стандартна висота, як на твоїх макетах
        shape = RoundedCornerShape(12.dp), // Таке ж закруглення, як у текстових полів
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue, // Наш неоновий синій
            contentColor = TextWhite,

            // Кольори для вимкненої кнопки (наприклад, коли поля ще не заповнені)
            disabledContainerColor = SurfaceDarkElevated,
            disabledContentColor = TextGray
        )
    ) {
        if (isLoading) {
            // Показуємо крутилку, якщо йде запит на сервер
            CircularProgressIndicator(
                color = TextWhite,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            // Звичайний текст
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}