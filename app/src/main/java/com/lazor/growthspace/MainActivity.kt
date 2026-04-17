package com.lazor.growthspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lazor.growthspace.ui.auth.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Тимчасово показуємо екран реєстрації при запуску додатку
            RegisterScreen(
                onRegisterSuccess = {},
                onBackClick = {},
                onLoginClick = {}
            )
        }
    }
}