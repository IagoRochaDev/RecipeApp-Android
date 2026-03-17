package com.devrochaiago.recipeapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.data.repository.MealRepository
import com.devrochaiago.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val meals: List<MealDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null,
    val favoriteIds: Set<String> = emptySet()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val categories = listOf("Beef", "Chicken", "Dessert", "Pasta", "Seafood", "Vegan", "Breakfast")
    val areas = listOf("Italian", "Mexican", "Japanese", "Indian", "French", "American")

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { list ->
                val favoriteIds = list.map { it.idMeal }.toSet()
                _uiState.update { it.copy(favoriteIds = favoriteIds) }
            }
        }
    }

    fun searchMeals(query: String) {
        _uiState.update { it.copy(searchQuery = query, selectedFilter = null) }

        if (query.isBlank()) {
            _uiState.update { it.copy(meals = emptyList(), isLoading = false, error = null) }
            return
        }

        viewModelScope.launch {
            val isCategory = categories.any { it.equals(query, ignoreCase = true) }
            val isArea = areas.any { it.equals(query, ignoreCase = true) }

            val flow = when {
                isCategory -> {
                    val exactCategory = categories.first { it.equals(query, ignoreCase = true) }
                    repository.getMealsByCategory(exactCategory)
                }
                isArea -> {
                    val exactArea = areas.first { it.equals(query, ignoreCase = true) }
                    repository.getMealsByArea(exactArea)
                }
                else -> {
                    repository.searchMeals(query)
                }
            }
            
            flow.collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Resource.Loading -> state.copy(isLoading = true, error = null)
                        is Resource.Success -> state.copy(isLoading = false, meals = result.data ?: emptyList(), error = null)
                        is Resource.Error -> state.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun filterByCategory(category: String) {
        _uiState.update { it.copy(searchQuery = category, selectedFilter = category) }
        viewModelScope.launch {
            repository.getMealsByCategory(category).collect { result ->
                updateSearchState(result)
            }
        }
    }

    fun filterByArea(area: String) {
        _uiState.update { it.copy(searchQuery = area, selectedFilter = area) }
        viewModelScope.launch {
            repository.getMealsByArea(area).collect { result ->
                updateSearchState(result)
            }
        }
    }

    private fun updateSearchState(result: Resource<List<MealDto>>) {
        _uiState.update { state ->
            when (result) {
                is Resource.Loading -> state.copy(isLoading = true, error = null)
                is Resource.Success -> state.copy(isLoading = false, meals = result.data ?: emptyList(), error = null)
                is Resource.Error -> state.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun toggleFavorite(meal: MealDto) {
        val isFavorite = _uiState.value.favoriteIds.contains(meal.id)
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = isFavorite)
        }
    }
}
