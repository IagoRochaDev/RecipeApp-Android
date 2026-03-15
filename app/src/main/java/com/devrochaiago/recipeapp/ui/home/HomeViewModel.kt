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

    private val _randomMealResource = MutableStateFlow<Resource<MealDto>>(Resource.Loading())
    
    private val _favoriteIds = repository.getFavorites()
        .map { list -> list.map { it.idMeal }.toSet() }

    val uiState: StateFlow<HomeUiState> = combine(_randomMealResource, _favoriteIds) { resource, favorites ->
        when (resource) {
            is Resource.Loading -> HomeUiState(isLoading = true, favoriteIds = favorites)
            is Resource.Success -> HomeUiState(meal = resource.data, favoriteIds = favorites)
            is Resource.Error -> HomeUiState(error = resource.message, favoriteIds = favorites)
        }
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
                _randomMealResource.value = result
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
