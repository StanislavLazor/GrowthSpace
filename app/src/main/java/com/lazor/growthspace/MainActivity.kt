package com.lazor.growthspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lazor.growthspace.navigation.AppNavGraph
import com.lazor.growthspace.ui.theme.BackgroundDark
import com.lazor.growthspace.ui.theme.GrowthSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // На весь екран
        enableEdgeToEdge()

        setContent {
            // Кастомна тема
            GrowthSpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}