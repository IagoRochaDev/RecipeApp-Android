package com.devrochaiago.recipeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.ui.theme.RecipeAppTheme
import com.devrochaiago.recipeapp.ui.viewmodels.HomeViewModel
import com.devrochaiago.recipeapp.util.Resource
import com.devrochaiago.recipeapp.util.shimmerEffect

@Composable
fun HomeScreen(
    onMealClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Observamos o estado que vem do ViewModel
    val state by viewModel.randomMealState.collectAsState()

    HomeScreenContent(
        state = state,
        onRefresh = { viewModel.fetchRandomMeal() },
        onSaveFavorite = { meal -> viewModel.saveToFavorites(meal) },
        onNavigateToDetail = onMealClick
    )
}

@Composable
fun HomeScreenContent(
    state: Resource<MealDto>,
    onRefresh: () -> Unit,
    onSaveFavorite: (MealDto) -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Receita do Dia \uD83C\uDF7D\uFE0F",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (state !is Resource.Loading) {
                    Button(onClick = onRefresh) {
                        Text("Novo")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { meal?.let { onNavigateToDetail(it.id) } },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = meal?.thumbnail,
                                contentDescription = "Foto de ${meal?.name}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = meal?.name ?: "Sem Nome",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = meal?.category ?: "Sem Categoria",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (meal != null) {
                                    IconButton(onClick = { onSaveFavorite(meal) }) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                                            contentDescription = "Guardar Favorito",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message ?: "Erro desconhecido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onRefresh, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealCardShimmer() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(200.dp)
                .shimmerEffect()
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(24.dp)
                    .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                    .shimmerEffect()
                )
            }
        }
    }
}
