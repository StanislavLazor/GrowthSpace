package com.lazor.growthspace.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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

    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is EditProfileState.Success && !isDataLoaded) {
            val user = (state as EditProfileState.Success).user
            name = user.name
            bio = user.bio
            specialization = user.specialization
            experience = user.experience
            price = user.price
            photoUrl = user.photoUrl
            isDataLoaded = true
        } else if (state is EditProfileState.Saved) {
            onSaveClick()
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Налаштування", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ЛОГІКА АВАТАРКИ З ІНІЦІАЛАМИ АБО ФОТО
            val finalImageUrl = if (photoUrl.isNotBlank()) {
                photoUrl // Якщо є посилання - показуємо його
            } else if (name.isNotBlank()) {
                // Якщо посилання немає, але є ім'я - генеруємо круті ініціали
                "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=0D8ABC&color=fff&size=256"
            } else {
                "" // Якщо зовсім порожньо
            }

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(SurfaceDarkElevated)
                    .border(2.dp, PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (finalImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = finalImageUrl,
                        contentDescription = "Аватар користувача",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ІНСТРУКЦІЯ ТА ПОЛЕ ДЛЯ ФОТО
            EditHeader("ФОТО ПРОФІЛЮ")
            EditCard {
                Text(
                    text = "💡 Знайдіть фото в інтернеті, затисніть його і виберіть «Копіювати адресу зображення». Вставте посилання нижче.",
                    color = TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                EditInputField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    label = "Посилання на фото (URL)",
                    icon = Icons.Default.Link
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // КАРТКА ОСНОВНИХ ДАНИХ
            EditHeader("ОСОБИСТІ ДАНІ")
            EditCard {
                EditInputField(value = name, onValueChange = { name = it }, label = "Ім'я", icon = Icons.Default.Person)
                HorizontalDivider(color = Color.White.copy(0.05f))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = { Text("Розкажіть про себе...", color = TextGray) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    singleLine = false
                )
            }

            // КАРТКА ДЛЯ КОУЧА
            val user = (state as? EditProfileState.Success)?.user
            if (user?.role == "coach") {
                Spacer(modifier = Modifier.height(24.dp))
                EditHeader("ПРОФЕСІЙНІ ДАНІ")
                EditCard {
                    EditInputField(value = specialization, onValueChange = { specialization = it }, label = "Спеціалізація", icon = Icons.Default.Work)
                    HorizontalDivider(color = Color.White.copy(0.05f))
                    EditInputField(value = experience, onValueChange = { experience = it }, label = "Досвід", icon = Icons.Default.Timeline)
                    HorizontalDivider(color = Color.White.copy(0.05f))
                    EditInputField(value = price, onValueChange = { price = it }, label = "Ціна", icon = Icons.Default.Payments)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { viewModel.saveProfile(name, bio, specialization, experience, price, photoUrl) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (state is EditProfileState.Loading) {
                    CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(24.dp))
                } else {
                    Text("Зберегти зміни", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun EditHeader(title: String) {
    Text(title, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 8.dp))
}

@Composable
fun EditCard(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(20.dp)).padding(8.dp), content = content)
}

@Composable
fun EditInputField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextGray) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        ),
        singleLine = true
    )
}