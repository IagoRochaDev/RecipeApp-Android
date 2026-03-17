package com.devrochaiago.recipeapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.data.repository.MealRepository
import com.devrochaiago.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow<String?>(null)
    private val _searchPart = MutableStateFlow(SearchUiState())
    
    private val _favoriteIds = repository.getFavorites()
        .map { list -> list.map { it.idMeal }.toSet() }

    val uiState: StateFlow<SearchUiState> = combine(
        _searchQuery,
        _selectedFilter,
        _searchPart,
        _favoriteIds
    ) { query, filter, searchState, favorites ->
        searchState.copy(
            searchQuery = query,
            selectedFilter = filter,
            favoriteIds = favorites
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    val categories = listOf("Beef", "Chicken", "Dessert", "Pasta", "Seafood", "Vegan", "Breakfast")
    val areas = listOf("Italian", "Mexican", "Japanese", "Indian", "French", "American")

    fun searchMeals(query: String) {
        _searchQuery.value = query
        _selectedFilter.value = null

        if (query.isBlank()) {
            _searchPart.update { it.copy(meals = emptyList(), isLoading = false, error = null) }
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
                _searchPart.update { state ->
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
        _searchQuery.value = category
        _selectedFilter.value = category
        viewModelScope.launch {
            repository.getMealsByCategory(category).collect { result ->
                _searchPart.update { state ->
                    when (result) {
                        is Resource.Loading -> state.copy(isLoading = true, error = null)
                        is Resource.Success -> state.copy(isLoading = false, meals = result.data ?: emptyList(), error = null)
                        is Resource.Error -> state.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun filterByArea(area: String) {
        _searchQuery.value = area
        _selectedFilter.value = area
        viewModelScope.launch {
            repository.getMealsByArea(area).collect { result ->
                _searchPart.update { state ->
                    when (result) {
                        is Resource.Loading -> state.copy(isLoading = true, error = null)
                        is Resource.Success -> state.copy(isLoading = false, meals = result.data ?: emptyList(), error = null)
                        is Resource.Error -> state.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun toggleFavorite(meal: MealDto) {
        val isFavorite = uiState.value.favoriteIds.contains(meal.id)
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = isFavorite)
        }
    }
}
