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
    photoUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(SurfaceDarkElevated),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank() && photoUrl.startsWith("http")) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Avatar $name",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = if (name.isNotBlank()) name.take(1).uppercase() else "?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}