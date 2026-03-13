package com.devrochaiago.recipeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devrochaiago.recipeapp.R
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.ui.components.MealCard
import com.devrochaiago.recipeapp.ui.viewmodels.FavoritesViewModel

@Composable
fun FavoritesScreen(
    onMealClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    
    // Mapeia MealEntity para MealDto para usar o componente comum
    val favoriteDtos = favorites.map { entity ->
        MealDto(
            id = entity.idMeal,
            name = entity.name,
            category = entity.category,
            thumbnail = entity.thumbnail,
            instructions = entity.instructions,
            youtubeUrl = null,
            ingredient1 = null, ingredient2 = null, ingredient3 = null, ingredient4 = null, ingredient5 = null,
            ingredient6 = null, ingredient7 = null, ingredient8 = null, ingredient9 = null, ingredient10 = null,
            ingredient11 = null, ingredient12 = null, ingredient13 = null, ingredient14 = null, ingredient15 = null,
            ingredient16 = null, ingredient17 = null, ingredient18 = null, ingredient19 = null, ingredient20 = null,
            measure1 = null, measure2 = null, measure3 = null, measure4 = null, measure5 = null,
            measure6 = null, measure7 = null, measure8 = null, measure9 = null, measure10 = null,
            measure11 = null, measure12 = null, measure13 = null, measure14 = null, measure15 = null,
            measure16 = null, measure17 = null, measure18 = null, measure19 = null, measure20 = null
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.favorites_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.favorites_empty), 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteDtos, key = { it.id }) { meal ->
                    MealCard(
                        meal = meal,
                        isFavorite = true,
                        onToggleFavorite = { 
                            val entity = favorites.find { it.idMeal == meal.id }
                            entity?.let { viewModel.removeFavorite(it) }
                        },
                        onNavigateToDetail = { onMealClick(meal.id) }
                    )
                }
            }
        }
    }
}
