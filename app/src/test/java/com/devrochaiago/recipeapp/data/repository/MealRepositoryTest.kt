package com.devrochaiago.recipeapp.data.repository

import com.devrochaiago.recipeapp.data.local.MealDao
import com.devrochaiago.recipeapp.data.local.MealEntity
import com.devrochaiago.recipeapp.data.remote.MealApi
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.data.remote.MealResponse
import com.devrochaiago.recipeapp.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class MealRepositoryTest {

    private lateinit var repository: MealRepository
    private lateinit var apiMock: MealApi
    private lateinit var daoMock: MealDao

    @Before
    fun setUp() {
        apiMock = mockk()
        daoMock = mockk()
        repository = MealRepository(apiMock, daoMock)
    }

    @Test
    fun `getRandomMeal deve retornar Sucesso quando a API retorna um prato`() = runTest {
        // Arrange
        val mealDto = createFakeMealDto("1", "Pizza")
        val response = MealResponse(meals = listOf(mealDto))
        coEvery { apiMock.getRandomMeal() } returns response

        // Act
        val results = repository.getRandomMeal()
        
        // Assert
        // O primeiro valor emitido é Loading, o segundo é Success
        val finalResult = results.first { it is Resource.Success }
        assertTrue(finalResult is Resource.Success)
        assertEquals("Pizza", (finalResult as Resource.Success).data?.name)
    }

    @Test
    fun `getRandomMeal deve retornar Erro quando a API retorna lista vazia`() = runTest {
        // Arrange
        val response = MealResponse(meals = emptyList())
        coEvery { apiMock.getRandomMeal() } returns response

        // Act
        val results = repository.getRandomMeal()
        
        // Assert
        val finalResult = results.first { it is Resource.Error }
        assertTrue(finalResult is Resource.Error)
        assertEquals("Nenhuma receita encontrada.", (finalResult as Resource.Error).message)
    }

    @Test
    fun `getRandomMeal deve retornar Erro de internet quando ocorre IOException`() = runTest {
        // Arrange
        coEvery { apiMock.getRandomMeal() } throws IOException()

        // Act
        val results = repository.getRandomMeal()
        
        // Assert
        val finalResult = results.first { it is Resource.Error }
        assertEquals("Sem conexão com a internet.", (finalResult as Resource.Error).message)
    }

    @Test
    fun `toggleFavorite deve deletar quando ja for favorito`() = runTest {
        // Arrange
        val mealDto = createFakeMealDto("1", "Pizza")
        coEvery { daoMock.deleteFavorite(any()) } returns Unit

        // Act
        repository.toggleFavorite(mealDto, isCurrentlyFavorite = true)

        // Assert
        coVerify(exactly = 1) { daoMock.deleteFavorite(any()) }
    }

    @Test
    fun `toggleFavorite deve inserir quando nao for favorito`() = runTest {
        // Arrange
        val mealDto = createFakeMealDto("1", "Pizza")
        coEvery { daoMock.insertFavorite(any()) } returns Unit

        // Act
        repository.toggleFavorite(mealDto, isCurrentlyFavorite = false)

        // Assert
        coVerify(exactly = 1) { daoMock.insertFavorite(any()) }
    }

    private fun createFakeMealDto(id: String, name: String): MealDto {
        return MealDto(
            id = id,
            name = name,
            category = "Italian",
            instructions = "Cook it",
            thumbnail = "url",
            youtubeUrl = null,
            ingredient1 = null, ingredient2 = null, ingredient3 = null, ingredient4 = null, ingredient5 = null,
            ingredient6 = null, ingredient7 = null, ingredient8 = null, ingredient9 = null, ingredient10 = null,
            ingredient11 = null, ingredient12 = null, ingredient13 = null, ingredient14 = null, ingredient15 = null,
            ingredient16 = null, ingredient17 = null, ingredient18 = null, ingredient19 = null, ingredient20 = null,
            measure1 = null, measure2 = null, measure3 = null, measure4 = null, measure5 = null,
            measure6 = null, measure7 = null, measure8 = null, measure9 = null, measure10 = null,
            measure11 = null, measure12 = null, measure13 = null, measure14 = null, measure15 = null,
            measure16 = null, measure17 = null, measure18 = null, measure19 = null, measure20 = null
        )
    }
}
