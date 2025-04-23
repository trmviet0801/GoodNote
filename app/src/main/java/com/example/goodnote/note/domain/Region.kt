package com.example.goodnote.note.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class Region(
    var primaryStroke: Stroke? = null,
    var boundary: Boundary? = null,
    var overlapsStrokes: List<Stroke> = emptyList(),
    var isDivided: Boolean = false,
    var isRoot: Boolean = false,
    var topLeftRegion: Region? = null,
    var topRightRegion: Region? = null,
    var bottomLeftRegion: Region? = null,
    var bottomRightRegion: Region? = null
) {
    fun addSize(amount: Offset, scale: Float = 1f): Region {
        val newBoundary = boundary!!.addSize(amount, scale)
        val newRegion = this.copy(boundary = newBoundary)
        newRegion.topLeftRegion?.addSize(amount, scale)
        newRegion.topRightRegion?.addSize(amount, scale)
        newRegion.bottomLeftRegion?.addSize(amount, scale)
        newRegion.bottomRightRegion?.addSize(amount, scale)
        return newRegion
    }

    //4 equal regions
    private fun divide() {
        if (!isDivided) {
            val boundaries = boundary!!.extractSubDivines()
            topLeftRegion = Region(boundary = boundaries[0])
            topRightRegion = Region(boundary = boundaries[1])
            bottomLeftRegion = Region(boundary = boundaries[2])
            bottomRightRegion = Region(boundary = boundaries[3])

            isDivided = true
        }
    }

    fun insert(stroke: Stroke) {
        if (primaryStroke == null) {
            if (boundary!!.contains(stroke)) {
                primaryStroke = stroke
            } else if (boundary!!.overlap(stroke)) updateOverlapsStrokes(stroke)
        } else {
            if (boundary!!.overlap(stroke)) {
                updateOverlapsStrokes(stroke)
                if (boundary!!.contains(stroke)) {
                    divide()
                    insertToAllSubDivines(stroke)
                }
            }
        }
    }

    private fun insertToAllSubDivines(stroke: Stroke) {
        topLeftRegion!!.insert(stroke)
        topRightRegion!!.insert(stroke)
        bottomLeftRegion!!.insert(stroke)
        bottomRightRegion!!.insert(stroke)
    }

    private fun updateOverlapsStrokes(stroke: Stroke): Unit {
        val currentOverlapsStrokes = overlapsStrokes.toMutableList()
        currentOverlapsStrokes.add(stroke)
        overlapsStrokes = currentOverlapsStrokes.toList()
    }

    fun scaleStrokes(scale: Float): Region {
        primaryStroke?.updateScaledPositions(scale)
        overlapsStrokes.forEach { stroke -> stroke.updateScaledPositions(scale) }
        return this
    }

    fun updateScaledPositions(scale: Float): Region {
        if (isRoot) {
            val newPrimaryStroke = primaryStroke!!.updateScaledPositions(scale)
            val overlapsStrokes: List<Stroke> =
                overlapsStrokes.map { stroke -> stroke.updateScaledPositions(scale) }
            return this.copy(
                primaryStroke = newPrimaryStroke,
                overlapsStrokes = overlapsStrokes
            )
        }
        return this
    }

    fun scroll(amount: Offset): Region {
        val newPrimaryStroke = primaryStroke?.scroll(amount)
        val newStrokes: List<Stroke> = overlapsStrokes.map { stroke -> stroke.scroll(amount) }
        return this.copy(
            primaryStroke = newPrimaryStroke,
            overlapsStrokes = newStrokes
        )
    }
}