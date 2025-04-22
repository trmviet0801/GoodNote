package com.example.goodnote.note.domain

import android.util.Log
import androidx.compose.runtime.Immutable

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
}