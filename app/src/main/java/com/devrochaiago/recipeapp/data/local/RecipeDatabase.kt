package com.devrochaiago.recipeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MealEntity::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract val mealDao: MealDao

    companion object {
        const val DATABASE_NAME = "recipe_db"
    }
}