package com.example.goodnote.note.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class Dot(
    val x: Float,
    val y: Float,
    var scaledX: Float,
    var scaledY: Float,
)

fun Dot.calScaledPosition(scale: Float) {
    this.scaledX = x * scale
    this.scaledY = y * scale
}

fun Dot.scroll(amount: Offset): Dot {
    return this.copy(
        x = x + amount.x,
        y = y + amount.y,
        scaledX = scaledX + amount.x,
        scaledY = scaledY + amount.y
    )
}