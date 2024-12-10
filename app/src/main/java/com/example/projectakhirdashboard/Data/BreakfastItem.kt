package com.example.projectakhirdashboard.Data

import androidx.compose.ui.graphics.Color

data class BreakfastItem(
    val title: String,
    val description: String,
    val timeEstimate: String,
    val imageRes: Int,
    val cardColor: Color,
    var isFavorite: Boolean = true
)