package com.devrochaiago.recipeapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devrochaiago.recipeapp.ui.navigation.Screen
import androidx.compose.ui.tooling.preview.Preview
import com.devrochaiago.recipeapp.ui.theme.RecipeAppTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.devrochaiago.recipeapp.ui.home.HomeScreen
import com.devrochaiago.recipeapp.ui.search.SearchScreen
import com.devrochaiago.recipeapp.ui.favorites.FavoritesScreen
import com.devrochaiago.recipeapp.ui.details.DetailScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(Screen.Home, Screen.Search, Screen.Favorites)

    Scaffold(
        bottomBar = {
            if (currentRoute != null && !currentRoute.startsWith("detail/")) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = stringResource(id = screen.titleResId)) },
                            label = { Text(stringResource(id = screen.titleResId)) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMealClick = { idReceita ->
                        navController.navigate(Screen.Detail.createRoute(idReceita))
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onMealClick = { idReceita ->
                        navController.navigate(Screen.Detail.createRoute(idReceita))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onMealClick = { idReceita ->
                        navController.navigate(Screen.Detail.createRoute(idReceita))
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("mealId") { type = NavType.StringType })
            ) {
                DetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RecipeAppTheme {
        MainScreen()
    }
}
