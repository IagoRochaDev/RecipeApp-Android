package com.devrochaiago.recipeapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.recipeapp.data.local.MealEntity
import com.devrochaiago.recipeapp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<MealEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = repository.getFavorites()
        .map { FavoritesUiState(favorites = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState(isLoading = true)
        )

    fun removeFavorite(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteFavorite(meal)
        }
    }
}
