package com.example.goodnote.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.goodnote.goodNote.domain.Dot
import com.example.goodnote.goodNote.domain.calScaledPosition
import com.example.goodnote.goodNote.utils.PenConst
import java.util.UUID
import kotlin.math.abs

@Entity(tableName = "strokes")
data class Stroke(
    @PrimaryKey
    val strokeId: String = UUID.randomUUID().toString(),
    var dots: List<Dot> = emptyList(),
    var bold: Float = 5f,
    var timestamp: Long = System.currentTimeMillis(),
    var isRightest: Boolean = false,
    var isDownest: Boolean = false,
    var color: Long = PenConst.DEFAULT_PEN_COLOR_1,
    val lineWidth: Float = PenConst.DEFAULT_LINE_WIDTH
)

fun Stroke.getRightest(): Dot? {
    if (dots.isEmpty()) return null
    return dots.maxByOrNull { it.x }
}

fun Stroke.getDownest(): Dot? {
    if (dots.isEmpty()) return null
    return dots.maxByOrNull { it.y }
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