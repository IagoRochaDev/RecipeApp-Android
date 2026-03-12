package com.devrochaiago.recipeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devrochaiago.recipeapp.ui.viewmodels.SearchViewModel
import com.devrochaiago.recipeapp.util.Resource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onMealClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    // Controlo do Bottom Sheet (O painel flutuante de filtros)
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. A BARRA DE PESQUISA COM ÍCONE DE FILTRO INCLUÍDO
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchMeals(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Procurar receita, categoria...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    // O Botão de Filtro Profissional
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Filtros"
                        )
                    }
                },
                shape = MaterialTheme.shapes.large, // Bordas bem arredondadas
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. A LISTA DE RESULTADOS
            when (searchState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Error -> {
                    Text(
                        text = searchState.message ?: "Erro desconhecido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is Resource.Success -> {
                    val meals = searchState.data ?: emptyList()
                    if (meals.isEmpty() && searchQuery.isNotEmpty()) {
                        Text(
                            text = "Nenhuma receita encontrada.",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(meals, key = { it.id }) { meal ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onMealClick(meal.id) },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = meal.thumbnail,
                                            contentDescription = meal.name,
                                            modifier = Modifier.size(100.dp)
                                        )
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(text = meal.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = meal.category ?: "",
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. O MODAL BOTTOM SHEET (FILTROS)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Filtros de Busca",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Categorias", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    viewModel.categories.forEach { category ->
                        FilterChip(
                            selected = selectedFilter == category,
                            onClick = {
                                viewModel.filterByCategory(category)
                                showBottomSheet = false
                            },
                            label = { Text(category) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Regiões (Cozinha)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    viewModel.areas.forEach { area ->
                        FilterChip(
                            selected = selectedFilter == area,
                            onClick = {
                                viewModel.filterByArea(area)
                                showBottomSheet = false
                            },
                            label = { Text(area) }
                        )
                    }
                }
            }
        }
    }
}
