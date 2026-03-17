package com.devrochaiago.recipeapp.ui.home

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

    private val _mealPart = MutableStateFlow(HomeUiState(isLoading = true))
    
    private val _favoriteIds = repository.getFavorites()
        .map { list -> list.map { it.idMeal }.toSet() }

    val uiState: StateFlow<HomeUiState> = combine(_mealPart, _favoriteIds) { state, favorites ->
        state.copy(favoriteIds = favorites)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    init {
        fetchRandomMeal()
    }

    fun fetchRandomMeal() {
        viewModelScope.launch {
            repository.getRandomMeal().collect { result ->
                _mealPart.update { state ->
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
        val isFavorite = uiState.value.favoriteIds.contains(meal.id)
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = isFavorite)
        }
    }
}
