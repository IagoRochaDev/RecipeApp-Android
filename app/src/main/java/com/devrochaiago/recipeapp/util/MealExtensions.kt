package com.devrochaiago.recipeapp.util

import com.devrochaiago.recipeapp.data.remote.MealDto


fun MealDto.getIngredientsList(): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()

    // Juntamos todos os campos em duas listas
    val ingredients = listOf(
        ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
        ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
        ingredient11, ingredient12, ingredient13, ingredient14, ingredient15,
        ingredient16, ingredient17, ingredient18, ingredient19, ingredient20
    )

    val measures = listOf(
        measure1, measure2, measure3, measure4, measure5,
        measure6, measure7, measure8, measure9, measure10,
        measure11, measure12, measure13, measure14, measure15,
        measure16, measure17, measure18, measure19, measure20
    )

    for (i in ingredients.indices) {
        val ingredient = ingredients[i]
        val measure = measures[i]

        if (!ingredient.isNullOrBlank()) {
            list.add(Pair(ingredient.trim(), measure?.trim() ?: ""))
        }
    }

    return list
}