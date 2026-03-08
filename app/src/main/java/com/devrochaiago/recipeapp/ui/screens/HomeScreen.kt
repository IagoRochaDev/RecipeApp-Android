package com.devrochaiago.recipeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    // Observamos o estado que vem do ViewModel
    val state by viewModel.randomMealState.collectAsState()

    HomeScreenContent(
        state = state,
        onRefresh = { viewModel.fetchRandomMeal() }
    )
}

@Composable
fun HomeScreenContent(
    state: Resource<MealDto>,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
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

            item {
                when (state) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Success -> {
                        val meal = state.data
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = meal?.thumbnail,
                                    contentDescription = "Foto de ${meal?.name}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                                Column(modifier = Modifier.padding(16.dp)) {
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
                            }
                        }
                    }
                    is Resource.Error -> {
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RecipeAppTheme {
        HomeScreenContent(
            state = Resource.Success(
                MealDto(
                    id = "1",
                    name = "Teriyaki Chicken Casserole",
                    category = "Chicken",
                    instructions = "Preheat oven to 350°F (175°C).",
                    thumbnail = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
                    youtubeUrl = null,
                    ingredient1 = "soy sauce",
                    ingredient2 = "water",
                    ingredient3 = "brown sugar",
                    ingredient4 = "ground ginger",
                    ingredient5 = "garlic powder",
                    measure1 = "3/4 cup",
                    measure2 = "1/2 cup",
                    measure3 = "1/4 cup",
                    measure4 = "1/2 teaspoon",
                    measure5 = "1/4 teaspoon"
                )
            ),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    RecipeAppTheme {
        HomeScreenContent(
            state = Resource.Loading(),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    RecipeAppTheme {
        HomeScreenContent(
            state = Resource.Error("Erro ao carregar receita"),
            onRefresh = {}
        )
    }
}