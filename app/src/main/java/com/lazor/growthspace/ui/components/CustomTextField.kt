package com.lazor.growthspace.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lazor.growthspace.ui.theme.*

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label, color = TextGray) },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = TextGray) }
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
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