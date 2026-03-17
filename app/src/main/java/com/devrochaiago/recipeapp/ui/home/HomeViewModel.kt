package com.devrochaiago.recipeapp.ui.home

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

data class HomeUiState(
    val meal: MealDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val favoriteIds: Set<String> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
        fetchRandomMeal()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { list ->
                val favoriteIds = list.map { it.idMeal }.toSet()
                _uiState.update { it.copy(favoriteIds = favoriteIds) }
            }
        }
    }

    fun fetchRandomMeal() {
        viewModelScope.launch {
            repository.getRandomMeal().collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Resource.Loading -> state.copy(isLoading = true, error = null, meal = null)
                        is Resource.Success -> state.copy(isLoading = false, meal = result.data, error = null)
                        is Resource.Error -> state.copy(isLoading = false, error = result.message, meal = null)
                    }
                }
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
