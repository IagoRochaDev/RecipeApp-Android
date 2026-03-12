package com.devrochaiago.recipeapp.viewmodel

import com.devrochaiago.recipeapp.data.repository.MealRepository
import com.devrochaiago.recipeapp.ui.viewmodels.SearchViewModel
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

    // O "Motor" falso para as Coroutines rodarem nos testes
    private val testDispatcher = StandardTestDispatcher()

    // 1. As nossas variáveis de teste
    private lateinit var viewModel: SearchViewModel
    private lateinit var repositoryMock: MealRepository // O nosso carteiro falso

    @Before
    fun setUp() {
        // PREPARAÇÃO ANTES DE CADA TESTE (Arrange)
        Dispatchers.setMain(testDispatcher) // Diz ao Android para usar o nosso motor de teste

        // Criamos um clone falso do Repository usando o MockK
        repositoryMock = mockk(relaxed = true)

        // Inicializamos o nosso ViewModel injetando o repository falso nele
        viewModel = SearchViewModel(repositoryMock)
    }

    @After
    fun tearDown() {
        // LIMPEZA APÓS CADA TESTE
        Dispatchers.resetMain()
    }

    @Test
    fun `Quando o utilizador busca por Dessert, deve chamar o endpoint de categorias`() = runTest(testDispatcher) {
        // Arrange (Preparar)
        // Dizemos ao nosso clone falso: "Se alguém te pedir a categoria Dessert, devolve um fluxo vazio"
        coEvery { repositoryMock.getMealsByCategory("Dessert") } returns flowOf()

        // Act (Agir)
        // Simulamos o utilizador a digitar "Dessert" na barra de pesquisa
        viewModel.searchMeals("Dessert")
        
        // Como o searchMeals lança uma corrotina, precisamos esperar que ela termine
        advanceUntilIdle()

        // Assert (Verificar)
        // Verificamos (Verify) se o ViewModel foi inteligente o suficiente para chamar a função de Categoria!
        coVerify(exactly = 1) {
            repositoryMock.getMealsByCategory("Dessert")
        }

        // Garantimos que ele NÃO chamou a função de busca normal
        coVerify(exactly = 0) {
            repositoryMock.searchMeals(any())
        }
    }
}
