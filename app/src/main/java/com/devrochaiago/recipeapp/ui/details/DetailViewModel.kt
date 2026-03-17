package com.devrochaiago.recipeapp.ui.details

import androidx.lifecycle.SavedStateHandle
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

data class DetailUiState(
    val meal: MealDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MealRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _mealId = savedStateHandle.get<String>("mealId") ?: ""

    init {
        observeFavoriteStatus()
        if (_mealId.isNotEmpty()) {
            getMealDetails(_mealId)
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            repository.getFavorites().collect { list ->
                val isFavorite = list.any { it.idMeal == _mealId }
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun getMealDetails(id: String) {
        viewModelScope.launch {
            repository.getMealById(id).collect { result ->
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
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = _uiState.value.isFavorite)
        }
    }
}
