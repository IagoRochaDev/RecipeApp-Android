package com.devrochaiago.recipeapp.ui.viewmodels

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MealRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _mealState = MutableStateFlow<Resource<MealDto>>(Resource.Loading())
    val mealState: StateFlow<Resource<MealDto>> = _mealState.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = repository.getFavorites()
        .map { list -> list.map { it.idMeal }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        savedStateHandle.get<String>("mealId")?.let { id ->
            getMealDetails(id)
        }
    }

    private fun getMealDetails(id: String) {
        viewModelScope.launch {
            repository.getMealById(id).collect { result ->
                _mealState.value = result
            }
        }
    }

    fun toggleFavorite(meal: MealDto) {
        val isFavorite = favoriteIds.value.contains(meal.id)
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = isFavorite)
        }
    }
}
