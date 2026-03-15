package com.devrochaiago.recipeapp.ui.details

import androidx.lifecycle.SavedStateHandle
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

    private val _mealResource = MutableStateFlow<Resource<MealDto>>(Resource.Loading())
    private val _mealId = savedStateHandle.get<String>("mealId") ?: ""

    private val _isFavorite = repository.getFavorites()
        .map { list -> list.any { it.idMeal == _mealId } }

    val uiState: StateFlow<DetailUiState> = combine(_mealResource, _isFavorite) { resource, favorite ->
        when (resource) {
            is Resource.Loading -> DetailUiState(isLoading = true, isFavorite = favorite)
            is Resource.Success -> DetailUiState(meal = resource.data, isFavorite = favorite)
            is Resource.Error -> DetailUiState(error = resource.message, isFavorite = favorite)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState(isLoading = true)
    )

    init {
        if (_mealId.isNotEmpty()) {
            getMealDetails(_mealId)
        }
    }

    private fun getMealDetails(id: String) {
        viewModelScope.launch {
            repository.getMealById(id).collect { result ->
                _mealResource.value = result
            }
        }
    }

    fun toggleFavorite(meal: MealDto) {
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = uiState.value.isFavorite)
        }
    }
}
