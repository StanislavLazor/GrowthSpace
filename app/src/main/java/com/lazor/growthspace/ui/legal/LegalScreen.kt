package com.lazor.growthspace.ui.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.theme.BackgroundDark
import com.lazor.growthspace.ui.theme.SurfaceDarkElevated
import com.lazor.growthspace.ui.theme.TextGray
import com.lazor.growthspace.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    title: String,
    content: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextWhite, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(start = 8.dp).background(SurfaceDarkElevated, CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite)
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = content,
                color = TextGray,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}