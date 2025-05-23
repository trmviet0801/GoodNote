package com.example.goodnote.goodNote.presentation.editor.core

import android.view.MotionEvent
import com.example.goodnote.goodNote.action.InsertAction
import com.example.goodnote.goodNote.action.StrokeAction
import com.example.goodnote.goodNote.domain.Dot
import com.example.goodnote.goodNote.domain.Stroke
import com.example.goodnote.goodNote.domain.getDownest
import com.example.goodnote.goodNote.domain.getRightest
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.model.StrokeBehavior
import com.example.goodnote.goodNote.presentation.model.pushBehavior
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun stylusHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    when (motionEvent.buttonState) {
        0 -> if (!state.value.isEraser) stylusWritingHandle(motionEvent, state) else eraseHandle(
            motionEvent,
            state
        )
        32 -> eraseHandle(motionEvent, state)
    }
}

private fun stylusWritingHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    val action = motionEvent.actionMasked
    when (action) {
        MotionEvent.ACTION_DOWN -> stylusWritingActionDownHandle(state)

        MotionEvent.ACTION_MOVE -> stylusWritingActionMoveHandle(motionEvent, state)

        MotionEvent.ACTION_UP -> stylusWritingActionUpHandle(false, state)
    }
}

//set up the up-coming stroke (empty dots, color, width)
private fun stylusWritingActionDownHandle(state: MutableStateFlow<EditorState>) {
    state.update { it ->
        val currentStroke = it.latestStroke
        var currentDots = currentStroke.dots.toMutableList()
        currentDots = mutableListOf<Dot>()

        it.copy(
            latestStroke = Stroke(
                dots = currentDots.toList(),
                color = it.color,
                lineWidth = it.lineWidth
            )
        )
    }
}

private fun stylusWritingActionMoveHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        val currentStroke = it.latestStroke
        val currentDots = currentStroke.dots.toMutableList()
        currentDots.add(
            convertMotionEventToDot(motionEvent, state)
        )
        it.copy(
            latestStroke = Stroke(
                dots = currentDots.toList(),
                color = it.color,
                lineWidth = it.lineWidth
            )
        )
    }
}

// scale make the position is the offset with scale = 1 (strokes are stored with scale = 1 too)
// x,y always the position with scale = 1f
// scaledX, scaledY are the position with the current scale
fun convertMotionEventToDot(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>): Dot {
    return Dot(
        (motionEvent.x + state.value.canvasRelativePosition.x) / state.value.scale,
        (motionEvent.y + state.value.canvasRelativePosition.y) / state.value.scale,
        motionEvent.x + state.value.canvasRelativePosition.x,
        motionEvent.y + state.value.canvasRelativePosition.y
    )
}

// storing new stroke to display in the canvas
// stored stoke is state.latestStroke
fun stylusWritingActionUpHandle(isUndo: Boolean = false, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var currentOversizeStroke: List<Stroke> = it.oversizeStrokes
        var currentLatestStroke = it.latestStroke
        val currentRootRegion = it.rootRegion
        //update the farest stroke in the root region
        var currentRightest: Stroke = getCurrentRightest(currentLatestStroke, state)
        var currentDownest: Stroke = getCurrentDownest(currentLatestStroke, state)
        //update new action for undo/redo
        if (!isUndo) storeNewStroke(currentLatestStroke, state)
        // insert new stroke into root region
        // if stroke is oversize -> add to oversize list for erasing behavior
        if (currentRootRegion?.insert(currentLatestStroke) == InsertAction.Oversize)
            currentOversizeStroke = currentOversizeStroke.plus(currentLatestStroke)
        //get ready for the next writing
        currentLatestStroke = Stroke()

        it.copy(
            latestStroke = currentLatestStroke,
            rootRegion = currentRootRegion,
            rightest = currentRightest,
            downest = currentDownest,
            oversizeStrokes = currentOversizeStroke,
        )
    }
}

private fun getCurrentRightest(stroke: Stroke, state: MutableStateFlow<EditorState>): Stroke {
    return if (isRightest(stroke, state)) {
        if (state.value.rightest !== null)
            state.value.rightest!!.isRightest = false
        stroke.isRightest = true
        stroke
    } else state.value.rightest
        ?: Stroke()
}
private fun getCurrentDownest(stroke: Stroke, state: MutableStateFlow<EditorState>): Stroke {
    return if (isDownest(stroke, state)) {
        if (state.value.downest !== null)
            state.value.downest!!.isDownest = false
        stroke.isDownest = true
        stroke
    } else state.value.downest
        ?: Stroke()
}

private fun isRightest(stroke: Stroke, state: MutableStateFlow<EditorState>): Boolean {
    if (state.value.rightest == null || state.value?.rightest?.dots == emptyList<Dot>()) return true
    return stroke.getRightest()!!.x > state.value.rightest!!.getRightest()!!.x
}

private fun isDownest(stroke: Stroke, state: MutableStateFlow<EditorState>): Boolean {
    if (state.value.downest == null || state.value?.downest?.dots == emptyList<Dot>()) return true
    return stroke.getDownest()!!.y > state.value.downest!!.getDownest()!!.y
}

private fun storeNewStroke(stroke: Stroke, state: MutableStateFlow<EditorState>) {
    state.value.undoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.WRITE, stroke.copy()))
}