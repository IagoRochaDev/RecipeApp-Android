package com.devrochaiago.recipeapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {

    // Retorna todas as categorias (Carnes, Vegano, Sobremesas...)
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    // Retorna uma receita aleatória para a nossa Home
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse

    // Busca os detalhes de uma receita específica pelo ID
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse

    // Pesquisa receitas pelo nome (ex: "Arrabiata")
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    companion object {
        const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}