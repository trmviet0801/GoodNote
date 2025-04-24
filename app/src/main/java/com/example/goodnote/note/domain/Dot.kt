package com.example.goodnote.note.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
//x,y are the position with scale = 1f - used for checking equals with other offset
//scaledX,scaledY are the position with scale != 1f - used for displaying
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