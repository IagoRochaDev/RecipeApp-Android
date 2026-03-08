package com.devrochaiago.recipeapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.data.repository.MealRepository
import com.devrochaiago.recipeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _randomMealState = MutableStateFlow<Resource<MealDto>>(Resource.Loading())
    val randomMealState: StateFlow<Resource<MealDto>> = _randomMealState.asStateFlow()

    init {
        fetchRandomMeal()
    }

    fun fetchRandomMeal() {
        viewModelScope.launch {
            repository.getRandomMeal().collect { result ->
                _randomMealState.value = result
            }
        }
    }
}