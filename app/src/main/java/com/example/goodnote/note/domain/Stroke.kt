package com.example.goodnote.note.domain

import androidx.compose.ui.graphics.Color

data class Stroke(
    var dots: List<Dot> = emptyList(),
    var color: Color = Color.Black,
    var bold: Float = 5f,
    var timestamp: Long = System.currentTimeMillis()
)
