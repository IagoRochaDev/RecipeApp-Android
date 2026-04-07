package com.devrochaiago.recipeapp.viewmodel

import com.devrochaiago.recipeapp.data.repository.MealRepository
import com.devrochaiago.recipeapp.ui.search.SearchViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SearchViewModel
    private lateinit var repositoryMock: MealRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repositoryMock = mockk(relaxed = true)
        
        // Mock getFavorites to avoid issues with init block
        coEvery { repositoryMock.getFavorites() } returns flowOf(emptyList())
        
        viewModel = SearchViewModel(repositoryMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Quando o utilizador busca por Dessert, deve chamar o endpoint de categorias`() = runTest(testDispatcher) {
        // Arrange
        coEvery { repositoryMock.getMealsByCategory("Dessert") } returns flowOf()

        // Act
        viewModel.searchMeals("Dessert")
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            repositoryMock.getMealsByCategory("Dessert")
        }

        coVerify(exactly = 0) {
            repositoryMock.searchMeals(any())
        }
    }
}
