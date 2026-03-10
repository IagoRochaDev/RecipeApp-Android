package com.devrochaiago.recipeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.List

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Search : Screen("search", "Buscar", Icons.Default.Search)
    object Favorites : Screen("favorites", "Favoritos", Icons.Default.Favorite)
    object Detail : Screen("detail/{mealId}", "Detalhes", Icons.Default.List) {
        fun createRoute(mealId: String) = "detail/$mealId"
    }
}