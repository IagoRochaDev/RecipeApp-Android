package com.devrochaiago.recipeapp.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.devrochaiago.recipeapp.R

sealed class Screen(val route: String, @StringRes val titleResId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Search : Screen("search", R.string.nav_search, Icons.Default.Search)
    object Favorites : Screen("favorites", R.string.nav_favorites, Icons.Default.Favorite)
    object Detail : Screen("detail/{mealId}", R.string.nav_details, Icons.Default.List) {
        fun createRoute(mealId: String) = "detail/$mealId"
    }
}
