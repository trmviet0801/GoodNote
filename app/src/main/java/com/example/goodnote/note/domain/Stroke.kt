package com.example.goodnote.note.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class Stroke(
    var dots: List<Dot> = emptyList(),
    var color: Color = Color.Black,
    var bold: Float = 5f,
    var timestamp: Long = System.currentTimeMillis()
)

fun Stroke.toPath(scale: Float): Path {
    updateScaledPositions(scale)
    if (dots.isEmpty()) return Path()
    val result = Path()
    result.moveTo(dots[0].scaledX, dots[0].scaledY)
    dots.forEach { dot ->
        result.lineTo(dot.scaledX, dot.scaledY)
    }
    return result
}

fun Stroke.toPath(): Path {
    if (dots.isEmpty()) return Path()

    val result = Path()
    result.moveTo(dots[0].scaledX, dots[0].scaledY)
    dots.forEach { dot ->
        result.lineTo(dot.scaledX, dot.scaledY)
    }
    return result
}

fun Stroke.updateScaledPositions(scale: Float): Stroke {
    dots.forEach { dot ->
        dot.calScaledPosition(scale)
    }
    return this
}