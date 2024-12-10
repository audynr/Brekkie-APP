package com.example.projectakhirdashboard.Data

data class Recipe(
    val id: String,
    val image: String,
    val title: String,
    val ingredient: String,
    val description: String,
    val time: String,
    val steps: List<Step>,
    var isFavorite: Boolean = false
)

data class Step(
    val id: String,
    val instruction: String
)


data class RecipeData(
    val recipes: Map<String, List<Recipe>>
)
