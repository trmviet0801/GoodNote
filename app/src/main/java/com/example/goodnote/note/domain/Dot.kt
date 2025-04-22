package com.example.goodnote.note.domain

import androidx.compose.runtime.Immutable

@Immutable
data class Dot(
    val x: Float,
    val y: Float,
    var scaledX: Float = 0f,
    var scaledY: Float = 0f,
)

fun Dot.calScaledPosition(scale: Float) {
    this.scaledX = x * scale
    this.scaledY = y * scale
}
