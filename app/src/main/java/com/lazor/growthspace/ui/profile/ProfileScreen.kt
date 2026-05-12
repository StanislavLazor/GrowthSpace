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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = profileState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
                is ProfileState.Error -> {
                    Text(text = state.message, color = StatusCanceled, fontSize = 16.sp)
                }
                is ProfileState.Success -> {
                    val user = state.user
                    val firstLetter = user.name.firstOrNull()?.uppercase() ?: "U"

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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp, bottom = 24.dp)
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .border(2.dp, PrimaryBlue, CircleShape)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceDark)
                                    ) {
                                        Text(firstLetter, color = TextWhite, fontSize = 40.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(SurfaceDark, CircleShape)
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                            .clip(CircleShape)
                                            .clickable { onEditAvatarClick() }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Avatar", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = user.name, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text(text = user.email, color = TextGray, fontSize = 14.sp)

                                if (user.bio.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Тонкий роздільник
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Заголовок "Про мене"
                                    Text(
                                        text = "Про мене",
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                        textAlign = TextAlign.Start
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Самий текст bio
                                    Text(
                                        text = user.bio,
                                        color = TextGray,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "НАЛАШТУВАННЯ",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceDarkElevated, RoundedCornerShape(24.dp))
                                .clip(RoundedCornerShape(24.dp))
                        ) {
                            val roleText = if (user.role == "coach") "Коуч" else "Клієнт"
                            SettingsItem(icon = Icons.Outlined.Person, title = "Ваша роль", value = roleText)
                            SettingsDivider()
                            SettingsItem(icon = Icons.Outlined.Description, title = "Умови користування", onClick = { onLegalClick("terms") })
                            SettingsDivider()
                            SettingsItem(icon = Icons.Outlined.Policy, title = "Політика компанії", onClick = { onLegalClick("policy") })
                            SettingsDivider()
                            SettingsItem(icon = Icons.Outlined.Notifications, title = "Сповіщення")
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.logout()
                                onLogoutClick()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color(0xFFE91E63).copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE91E63))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Вийти з акаунту", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
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