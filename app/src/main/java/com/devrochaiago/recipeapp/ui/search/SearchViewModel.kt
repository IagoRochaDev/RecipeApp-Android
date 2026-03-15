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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<Resource<List<MealDto>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<MealDto>>> = _searchState.asStateFlow()

    private val _selectedFilter = MutableStateFlow<String?>(null)
    val selectedFilter: StateFlow<String?> = _selectedFilter.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = repository.getFavorites()
        .map { list -> list.map { it.idMeal }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val categories = listOf("Beef", "Chicken", "Dessert", "Pasta", "Seafood", "Vegan", "Breakfast")
    val areas = listOf("Italian", "Mexican", "Japanese", "Indian", "French", "American")

    fun searchMeals(query: String) {
        _searchQuery.value = query
        _selectedFilter.value = null

        if (query.isBlank()) {
            _searchState.value = Resource.Success(emptyList())
            return
        }

        viewModelScope.launch {
            val isCategory = categories.any { it.equals(query, ignoreCase = true) }
            val isArea = areas.any { it.equals(query, ignoreCase = true) }

            when {
                isCategory -> {
                    val exactCategory = categories.first { it.equals(query, ignoreCase = true) }
                    repository.getMealsByCategory(exactCategory).collect { _searchState.value = it }
                }
                isArea -> {
                    val exactArea = areas.first { it.equals(query, ignoreCase = true) }
                    repository.getMealsByArea(exactArea).collect { _searchState.value = it }
                }
                else -> {
                    repository.searchMeals(query).collect { _searchState.value = it }
                }
            }
        }
    }

    fun filterByCategory(category: String) {
        _searchQuery.value = category
        _selectedFilter.value = category
        viewModelScope.launch {
            repository.getMealsByCategory(category).collect { _searchState.value = it }
        }
    }

    fun filterByArea(area: String) {
        _searchQuery.value = area
        _selectedFilter.value = area
        viewModelScope.launch {
            repository.getMealsByArea(area).collect { _searchState.value = it }
        }
    }

    fun toggleFavorite(meal: MealDto) {
        val isFavorite = favoriteIds.value.contains(meal.id)
        viewModelScope.launch {
            repository.toggleFavorite(meal, isCurrentlyFavorite = isFavorite)
        }
    }
}
