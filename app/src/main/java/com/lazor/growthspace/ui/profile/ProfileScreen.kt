package com.lazor.growthspace.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lazor.growthspace.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    onEditAvatarClick: () -> Unit = {},
    onLegalClick: (String) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Профіль", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = profileState) {
                is ProfileState.Loading -> CircularProgressIndicator(color = PrimaryBlue)
                is ProfileState.Error -> Text(text = state.message, color = StatusCanceled)
                is ProfileState.Success -> {
                    val user = state.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(SurfaceDarkElevated, Color(0xFF162B44).copy(alpha = 0.5f))
                                    ),
                                    shape = RoundedCornerShape(32.dp)
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .border(2.dp, PrimaryBlue, CircleShape)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (user.photoUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = user.photoUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(user.name.take(1).uppercase(), color = TextWhite, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = user.name, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text(text = user.email, color = TextGray, fontSize = 14.sp)

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onEditAvatarClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                    modifier = Modifier.height(44.dp).padding(horizontal = 24.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Редагувати профіль", color = TextWhite, fontSize = 14.sp)
                                }

                                if (user.role == "coach") {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(user.specialization.ifBlank { "Коуч" }, color = PrimaryBlue, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                        InfoStatColumn(label = "Досвід", value = user.experience.ifBlank { "—" })
                                        InfoStatColumn(label = "Ціна", value = user.price.ifBlank { "—" })
                                    }
                                }

                                if (user.bio.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 24.dp))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Про мене", color = TextWhite, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))
                                    Text(user.bio, color = TextGray, fontSize = 14.sp, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text("НАЛАШТУВАННЯ", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth().background(SurfaceDarkElevated, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))) {
                            SettingsItem(icon = Icons.Outlined.Person, title = "Ваша роль", value = if (user.role == "coach") "Коуч" else "Клієнт")
                            SettingsDivider()
                            SettingsItem(icon = Icons.Outlined.Description, title = "Умови користування", onClick = { onLegalClick("terms") })
                            SettingsDivider()
                            SettingsItem(icon = Icons.Outlined.Policy, title = "Політика компанії", onClick = { onLegalClick("policy") })
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                        OutlinedButton(
                            onClick = { viewModel.logout(); onLogoutClick() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color(0xFFE91E63).copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE91E63))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Вийти з акаунту", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

// ДОДАЙ ЦІ КОМПОНЕНТИ ВНИЗУ ФАЙЛУ (Їх не вистачає на скриншоті):

@Composable
fun InfoStatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextGray, fontSize = 12.sp)
        Text(text = value, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String? = null, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundDark, thickness = 1.dp)
}