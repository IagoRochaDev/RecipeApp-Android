package com.devrochaiago.recipeapp.data.remote

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    val categories: List<CategoryDto>
)
data class CategoryDto(
    @SerializedName("idCategory") val id: String,
    @SerializedName("strCategory") val name: String,
    @SerializedName("strCategoryThumb") val thumbnail: String,
    @SerializedName("strCategoryDescription") val description: String
)