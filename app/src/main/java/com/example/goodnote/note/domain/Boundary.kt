package com.example.goodnote.note.domain

import androidx.compose.ui.geometry.Offset

data class Boundary(
    var x: Float,
    var y: Float,
    var w: Float,
    var h: Float,
    var actualWidth: Float = 0f,
    var actualHeight: Float = 0f
)

fun Boundary.calActualSize(scale: Float): Boundary {
    actualWidth = (x + w) * scale
    actualHeight = (y + h) * scale
    return this
}

fun Boundary.calActualSize(): Boundary {
    actualWidth = x + w
    actualHeight = y + h
    return this
}

fun Boundary.contains(dot: Dot): Boolean {
    val minX = x - w
    val minY = y - h
    val maxX = x + w
    val maxY = y + h
    return (dot.x in minX..maxX) && (dot.y in minY..maxY)
}

fun Boundary.contains(stroke: Stroke): Boolean {
    stroke.dots.forEach { dot -> if(!contains(dot)) return false }
    return true
}

fun Boundary.overlap(stroke: Stroke): Boolean {
    stroke.dots.forEach { dot -> if (contains(dot)) return true }
    return false
}

// TopLeft -> TopRight -> BottomLeft -> BottomRight
fun Boundary.extractSubDivines(): List<Boundary> {
    val result: MutableList<Boundary> = mutableListOf()
    result.add(Boundary(
        this.x - (this.w / 2),
        this.y - (this.h / 2),
        this.w / 2,
        this.h / 2
    ).calActualSize())
    result.add(Boundary(
        this.x + (this.w / 2),
        this.y - (this.h / 2),
        this.w / 2,
        this.h / 2,
    ).calActualSize())
    result.add(Boundary(
        this.x - (this.w / 2),
        this.y + (this.h / 2),
        this.w / 2,
        this.h / 2,
    ).calActualSize())
    result.add(Boundary(
        this.x + (this.w / 2),
        this.y + (this.h / 2),
        this.w / 2,
        this.h / 2,
    ).calActualSize())
    return result.toList()
}