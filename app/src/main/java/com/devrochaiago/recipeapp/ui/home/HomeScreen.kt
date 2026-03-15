package com.devrochaiago.recipeapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devrochaiago.recipeapp.R
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.ui.components.MealCard
import com.devrochaiago.recipeapp.ui.components.MealCardShimmer
import com.devrochaiago.recipeapp.util.Resource

@Composable
fun HomeScreen(
    onMealClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.randomMealState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    HomeScreenContent(
        state = state,
        favoriteIds = favoriteIds,
        onRefresh = { viewModel.fetchRandomMeal() },
        onToggleFavorite = { meal -> viewModel.toggleFavorite(meal) },
        onNavigateToDetail = onMealClick
    )
}

@Composable
fun HomeScreenContent(
    state: Resource<MealDto>,
    favoriteIds: Set<String>,
    onRefresh: () -> Unit,
    onToggleFavorite: (MealDto) -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.home_recipe_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(id = R.string.home_recipe_title),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (state !is Resource.Loading) {
                    Button(
                        onClick = onRefresh,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_button_new),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        when (state) {
            is Resource.Loading -> {
                items(1) {
                    MealCardShimmer()
                }
            }
            is Resource.Success -> {
                item {
                    val meal = state.data
                    if (meal != null) {
                        MealCard(
                            meal = meal,
                            isFavorite = favoriteIds.contains(meal.id),
                            onToggleFavorite = { onToggleFavorite(meal) },
                            onNavigateToDetail = { onNavigateToDetail(meal.id) }
                        )
                    }
                }
            }
            is Resource.Error -> {
                item {
                    Text(
                        text = state.message ?: stringResource(id = R.string.error_unknown),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
