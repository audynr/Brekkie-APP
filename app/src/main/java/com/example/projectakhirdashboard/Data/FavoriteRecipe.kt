package com.example.projectakhirdashboard.Data

data class FavoriteRecipe(
    val id: String = "",
    var title: String = "",
    val description: String = "",
    val ingredients: String = "",
    val time: String = "",
    val image: String = "",
    var isFavorite: Boolean = false
)