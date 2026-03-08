package com.devrochaiago.recipeapp.data.repository

import com.devrochaiago.recipeapp.data.local.MealDao
import com.devrochaiago.recipeapp.data.local.MealEntity
import com.devrochaiago.recipeapp.data.remote.MealApi
import com.devrochaiago.recipeapp.data.remote.MealDto
import com.devrochaiago.recipeapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    }

    fun getFavorites(): Flow<List<MealEntity>> {
        return dao.getAllFavorites()
    }

    suspend fun toggleFavorite(meal: MealDto, isCurrentlyFavorite: Boolean) {
        val entity = MealEntity(
            idMeal = meal.id,
            name = meal.name,
            category = meal.category,
            thumbnail = meal.thumbnail,
            instructions = meal.instructions
        )

        if (isCurrentlyFavorite) {
            dao.deleteFavorite(entity)
        } else {
            dao.insertFavorite(entity)
        }
    }
}