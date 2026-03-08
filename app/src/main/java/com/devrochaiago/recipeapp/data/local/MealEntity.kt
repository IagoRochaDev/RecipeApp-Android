package com.devrochaiago.recipeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_meals")
data class MealEntity(
    @PrimaryKey
    val idMeal: String,
    val name: String,
    val category: String?,
    val thumbnail: String?,
    val instructions: String?
)
