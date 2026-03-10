package com.devrochaiago.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.devrochaiago.recipeapp.ui.theme.RecipeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.devrochaiago.recipeapp.ui.screens.MainScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                    MainScreen()
            }
        }
    }
}
