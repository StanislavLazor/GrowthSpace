package com.lazor.growthspace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lazor.growthspace.ui.theme.SurfaceDarkElevated

@Composable
fun UserAvatar(
    photoUrl: String,
    name: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape // За замовчуванням круглий, але можна змінити!
) {
    val finalImageUrl = if (photoUrl.isNotBlank()) {
        photoUrl
    } else if (name.isNotBlank()) {
        "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=0D8ABC&color=fff&size=256"
    } else {
        ""
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(SurfaceDarkElevated),
        contentAlignment = Alignment.Center
    ) {
        if (finalImageUrl.isNotEmpty()) {
            AsyncImage(
                model = finalImageUrl,
                contentDescription = "Аватар $name",
                modifier = Modifier.fillMaxSize().clip(shape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}