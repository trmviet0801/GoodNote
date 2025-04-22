package com.example.goodnote.note.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class Stroke(
    var dots: List<Dot> = emptyList(),
    var color: Color = Color.Black,
    var bold: Float = 5f,
    var timestamp: Long = System.currentTimeMillis()
)

fun Stroke.toPath(): Path {
    if (dots.isEmpty()) return Path()
    val result = Path()
    result.moveTo(dots[0].x, dots[0].y)
    dots.forEach { dot ->
        result.lineTo(dot.x, dot.y)
    }
    return result
}