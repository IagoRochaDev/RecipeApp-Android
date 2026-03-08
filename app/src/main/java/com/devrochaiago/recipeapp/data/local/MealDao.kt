package com.devrochaiago.recipeapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: MealEntity)

    @Delete
    suspend fun deleteFavorite(meal: MealEntity)

    @Query("SELECT * FROM favorite_meals")
    fun getAllFavorites(): Flow<List<MealEntity>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_meals WHERE idMeal = :id)")
    fun isFavorite(id: String): Flow<Boolean>
}