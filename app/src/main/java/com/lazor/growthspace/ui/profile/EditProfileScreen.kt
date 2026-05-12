package com.lazor.growthspace.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazor.growthspace.ui.components.CustomTextField
import com.lazor.growthspace.ui.components.PrimaryButton
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Стейт для полів
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Спостерігаємо за станом з ViewModel
    LaunchedEffect(state) {
        when (state) {
            is EditProfileState.Success -> {
                if (!isDataLoaded) {
                    val user = (state as EditProfileState.Success).user
                    name = user.name
                    bio = user.bio // Підтягуємо біо з БД
                    isDataLoaded = true
                }
            }
            is EditProfileState.Saved -> {
                Toast.makeText(context, "Профіль успішно оновлено!", Toast.LENGTH_SHORT).show()
                onSaveClick() // Повертає нас на попередній екран
            }
            is EditProfileState.Error -> {
                Toast.makeText(context, (state as EditProfileState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редагування", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Крутилка при першому завантаженні даних
            if (state is EditProfileState.Loading && !isDataLoaded) {
                CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()) // Додали скрол, бо полів стало більше
                ) {
                    Text(
                        text = "Особисті дані",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Ім'я та Прізвище"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Додаткова інформація",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    CustomTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = "Про себе (Bio)"
                    )

                    // Spacer.weight(1f) тут працює погано разом із verticalScroll,
                    // тому просто робимо фіксований відступ перед кнопкою
                    Spacer(modifier = Modifier.height(40.dp))

                    PrimaryButton(
                        text = "Зберегти зміни",
                        isEnabled = name.isNotBlank() && state !is EditProfileState.Loading,
                        onClick = {
                            // Передаємо ім'я, і біо у ViewModel
                            viewModel.saveProfile(name, bio)
                        }
                    )
                }
            }

            // Оверлей завантаження під час натискання кнопки "Зберегти"
            if (state is EditProfileState.Loading && isDataLoaded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        }
    }
}