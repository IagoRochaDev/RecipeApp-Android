package com.devrochaiago.recipeapp.ui.viewmodels

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MealRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _mealState = MutableStateFlow<Resource<MealDto>>(Resource.Loading())
    val mealState: StateFlow<Resource<MealDto>> = _mealState.asStateFlow()

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
}