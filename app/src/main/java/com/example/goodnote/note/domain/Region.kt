package com.example.goodnote.note.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.note.action.InsertAction

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
        newRegion.adjustSubRegionSize()
        return newRegion
    }

    private fun adjustSubRegionSize() {
        val boundaries = boundary!!.extractSubDivines()
        if (topLeftRegion != null) topLeftRegion = topLeftRegion!!.copy(boundary = boundaries[0])
        if (topRightRegion != null) topRightRegion = topRightRegion!!.copy(boundary = boundaries[1])
        if (bottomLeftRegion != null) bottomLeftRegion = bottomLeftRegion!!.copy(boundary = boundaries[2])
        if (bottomRightRegion != null) bottomRightRegion = bottomRightRegion!!.copy(boundary = boundaries[3])
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

    fun insert(stroke: Stroke): InsertAction {
        if (primaryStroke == null || primaryStroke?.dots?.isEmpty() == true) {
            if (boundary!!.contains(stroke)) {
                primaryStroke = stroke
                return InsertAction.Inserted
            } else if (boundary!!.overlap(stroke)) {
                updateOverlapsStrokes(stroke)
                return InsertAction.Oversize
            }
            return InsertAction.NotRelevant
        } else {
            if (boundary!!.overlap(stroke)) {
                if (boundary!!.contains(stroke)) {
                    divide()
                    insertToAllSubDivines(stroke)
                }
                updateOverlapsStrokes(stroke)
                return InsertAction.Oversize
            }
            return InsertAction.NotRelevant
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

    fun removeStrokes(strokes: List<Stroke>) {
        if (strokes.isEmpty()) return
        strokes.forEach { stroke -> stroke.dots = emptyList<Dot>() }
    }

    fun findStrokesToRemove(dot: Dot, removedStrokes: MutableList<Stroke>): MutableList<Stroke> {
        if (boundary?.contains(dot) == true) {
            if (primaryStroke?.contains(dot) == true) {
                removedStrokes.add(primaryStroke!!)
            } else {
                removeInSubRegions(dot, removedStrokes)
            }
        }
        return removedStrokes
    }

    private fun removeInSubRegions(dot: Dot, removedStrokes: MutableList<Stroke>) {
        this.topLeftRegion?.findStrokesToRemove(dot, removedStrokes)
        this.topRightRegion?.findStrokesToRemove(dot, removedStrokes)
        this.bottomLeftRegion?.findStrokesToRemove(dot, removedStrokes)
        this.bottomRightRegion?.findStrokesToRemove(dot, removedStrokes)
    }

    //return rightest stroke in region
    //empty region return null
    fun findRightestStroke(): Stroke? {
        if (isRoot) {
            var rightest: Stroke? = null
            if (primaryStroke == null && overlapsStrokes.isEmpty())
                return null
            rightest = primaryStroke ?: overlapsStrokes[0]
            overlapsStrokes.forEach { stroke ->
                if (rightest!!.findRightestDot() < stroke.findRightestDot())
                    rightest = stroke
            }
            return rightest
        }
        return null
    }

    // return downest stroke in region
    //empty region returns null
    fun findDownestStroke(): Stroke? {
        if (isRoot) {
            var downest: Stroke? = null
            if (primaryStroke == null && overlapsStrokes.isEmpty())
                return null
            downest = primaryStroke ?: overlapsStrokes[0]
            overlapsStrokes.forEach { stroke ->
                if (downest!!.findDownestDot() < stroke.findDownestDot())
                    downest = stroke
            }
        }
        return null
    }
}