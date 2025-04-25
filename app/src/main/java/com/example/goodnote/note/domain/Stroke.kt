package com.example.goodnote.note.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.abs

data class Stroke(
    var dots: List<Dot> = emptyList(),
    var color: Color = Color.Black,
    var bold: Float = 5f,
    var timestamp: Long = System.currentTimeMillis(),
    var isRightest: Boolean = false,
    var isDownest: Boolean = false
)

fun Stroke.getRightest(): Dot? {
    if (dots.isEmpty()) return null
    return dots.maxByOrNull { it.x }
}

fun Stroke.getDownest(): Dot? {
    if (dots.isEmpty()) return null
    return dots.maxByOrNull { it.y }
}

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


fun Stroke.toPath(virtualCamera: Offset): Path {
    if (dots.isEmpty()) return Path()

    val result = Path()
    result.moveTo(dots[0].scaledX - virtualCamera.x, dots[0].scaledY - virtualCamera.y)
    dots.forEach { dot ->
        result.lineTo(dot.scaledX - virtualCamera.x, dot.scaledY - virtualCamera.y)
    }
    return result
}

fun Stroke.updateScaledPositions(scale: Float): Stroke {
    dots.forEach { dot ->
        dot.calScaledPosition(scale)
    }
    return this
}

//check surrounding area because of the very small size of px
fun Stroke.contains(dot: Dot): Boolean {
    return dots.any { it ->
        val dx = abs(dot.x - it.x)
        val dy = abs(dot.y - it.y)
        dx <= 10 && dy <= 10
    }
}

fun Stroke.findRightestDot(): Float {
    var maxX = 0f
    if (!dots.isEmpty())
        dots.forEach { dot ->
            if (dot.x > maxX) maxX = dot.x
        }
    return maxX
}

fun Stroke.findDownestDot(): Float {
    var maxY = 0f
    if (!dots.isEmpty())
        dots.forEach { it ->
            if (it.y > maxY) maxY = it.x
        }
    return maxY
}