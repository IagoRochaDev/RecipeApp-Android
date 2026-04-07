package com.devrochaiago.recipeapp.data.repository

import com.devrochaiago.recipeapp.data.local.MealDao
import com.devrochaiago.recipeapp.data.local.MealEntity
import com.devrochaiago.recipeapp.data.remote.MealApi
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MealRepository @Inject constructor(
    private val api: MealApi,
    private val dao: MealDao
) {
    fun getRandomMeal(): Flow<Resource<MealDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getRandomMeal()
            val meal = response.meals?.firstOrNull()
            if (meal != null) {
                emit(Resource.Success(meal))
            } else {
                emit(Resource.Error("Nenhuma receita encontrada."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Erro no servidor da API."))
        } catch (e: IOException) {
            emit(Resource.Error("Sem conexão com a internet."))
        }
    }.flowOn(Dispatchers.IO)

    fun getFavorites(): Flow<List<MealEntity>> {
        return dao.getAllFavorites().flowOn(Dispatchers.IO)
    }

    suspend fun deleteFavorite(meal: MealEntity) = withContext(Dispatchers.IO) {
        dao.deleteFavorite(meal)
    }

    fun searchMeals(query: String): Flow<Resource<List<MealDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.searchMeals(query)
            val meals = response.meals ?: emptyList()
            emit(Resource.Success(meals))
        } catch (e: HttpException) {
            emit(Resource.Error("Erro no servidor da API."))
        } catch (e: IOException) {
            emit(Resource.Error("Sem conexão com a internet."))
        }
    }.flowOn(Dispatchers.IO)

    fun getMealsByCategory(category: String): Flow<Resource<List<MealDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMealsByCategory(category)
            val meals = response.meals ?: emptyList()
            emit(Resource.Success(meals))
        } catch (e: Exception) {
            emit(Resource.Error("Erro ao filtrar por categoria: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun getMealsByArea(area: String): Flow<Resource<List<MealDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMealsByArea(area)
            val meals = response.meals ?: emptyList()
            emit(Resource.Success(meals))
        } catch (e: Exception) {
            emit(Resource.Error("Erro ao filtrar por região: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun toggleFavorite(meal: MealDto, isCurrentlyFavorite: Boolean) = withContext(Dispatchers.IO) {
        val entity = MealEntity(
            idMeal = meal.id,
            name = meal.name,
            category = meal.category ?: "",
            thumbnail = meal.thumbnail ?: "",
            instructions = meal.instructions ?: ""
        )

        if (isCurrentlyFavorite) {
            dao.deleteFavorite(entity)
        } else {
            dao.insertFavorite(entity)
        }
    }

    fun getMealById(id: String): Flow<Resource<MealDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMealById(id)
            val meal = response.meals?.firstOrNull()
            if (meal != null) {
                emit(Resource.Success(meal))
            } else {
                emit(Resource.Error("Receita não encontrada."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Erro no servidor da API."))
        } catch (e: IOException) {
            emit(Resource.Error("Sem conexão com a internet."))
        }
    }.flowOn(Dispatchers.IO)
}
