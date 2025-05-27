package com.example.goodnote.goodNote.presentation.editor.core

import android.view.MotionEvent
import com.example.goodnote.goodNote.action.StrokeAction
import com.example.goodnote.goodNote.domain.Dot
import com.example.goodnote.domain.Stroke
import com.example.goodnote.domain.contains
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.model.StrokeBehavior
import com.example.goodnote.goodNote.presentation.model.pushBehavior
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun eraseHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    val action = motionEvent.actionMasked
    when (action) {
        MotionEvent.ACTION_DOWN -> eraseActionDownHandle(motionEvent, state)
        MotionEvent.ACTION_MOVE -> eraseActionMoveHandle(motionEvent, state)
        MotionEvent.ACTION_UP -> eraseActionUpHandle(state)
    }
    updateFarestStrokes(state)
}

private fun eraseActionDownHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var currentRemoveStroke = it.removedStrokes
        if (!currentRemoveStroke.isEmpty()) currentRemoveStroke = emptyList()
        //remove oversize strokes
        val currentOversizeStrokes =
            removeOversizeStrokes(convertMotionEventToDot(motionEvent, state), state = state)
        //find strokes to remove
        currentRemoveStroke = currentRemoveStroke.plus(
            it.rootRegion!!.findStrokesToRemove(
                convertMotionEventToDot(motionEvent, state),
                currentRemoveStroke.toMutableList()
            ).toList()
        )
        //storing removed strokes for undo/redo
        currentRemoveStroke.forEach { stroke -> storeErasedStroke(stroke, state) }

        //removing after getting all mapped strokes
        if (!currentRemoveStroke.isEmpty()) it.rootRegion!!.removeStrokes(
            currentRemoveStroke
        )
        it.copy(
            removedStrokes = currentRemoveStroke,
            oversizeStrokes = currentOversizeStrokes
        )
    }
}

private fun eraseActionMoveHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var currentRemoveStroke = it.removedStrokes
        //remove oversize strokes
        val currentOversizeStrokes =
            removeOversizeStrokes(convertMotionEventToDot(motionEvent, state), state = state)
        currentRemoveStroke = currentRemoveStroke.plus(
            it.rootRegion!!.findStrokesToRemove(
                convertMotionEventToDot(motionEvent, state),
                currentRemoveStroke.toMutableList()
            ).toList()
        )

        //remove stroke
        if (!currentRemoveStroke.isEmpty()) it.rootRegion!!.removeStrokes(
            currentRemoveStroke
        )
        it.copy(
            removedStrokes = currentRemoveStroke,
            oversizeStrokes = currentOversizeStrokes
        )
    }
}

//change dots to empty
//remove in oversize list + store removed strokes for undo/redo
fun removeOversizeStrokes(dot: Dot, isStoreUndo: Boolean = true, state: MutableStateFlow<EditorState>): List<Stroke> {
    var result = mutableListOf<Stroke>()
    var currentOversizeStrokes = state.value.oversizeStrokes

    currentOversizeStrokes.forEach { stroke ->
        if (stroke.contains(dot)) {
            if (isStoreUndo) storeErasedStroke(stroke, state)
            stroke.dots = emptyList<Dot>()
        } else {
            result.add(stroke)
        }
    }
    return result.toList()
}

private fun storeErasedStroke(stroke: Stroke, state: MutableStateFlow<EditorState>) {
    state.value.undoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.ERASE, stroke.copy()))
}

private fun eraseActionUpHandle(state: MutableStateFlow<EditorState>) {
    state.update { it ->
        it.copy(
            removedStrokes = emptyList<Stroke>()
        )
    }
}

//checking if need to update rightest stroke or downest stroke
//if need -> update
private fun updateFarestStrokes(state: MutableStateFlow<EditorState>) {
    var rightestStroke: Stroke? = null
    var downestStroke: Stroke? = null
    if (state.value.rightest == null || state.value.rightest?.dots?.isEmpty() == true)
        rightestStroke = findRightestStroke(state)
    if (state.value.downest == null || state.value.downest?.dots?.isEmpty() == true)
        downestStroke = findDownestStroke(state)
    state.update { it ->
        it.copy(
            rightest = rightestStroke ?: it.rightest,
            downest = downestStroke ?: it.downest
        )
    }
}

//update rightest stroke after erasing for keeping scrolling behavior
// if erased stroke was the rightest
private fun findRightestStroke(state: MutableStateFlow<EditorState>): Stroke? {
    return state.value.rootRegion?.findRightestStroke()
}

private fun findDownestStroke(state: MutableStateFlow<EditorState>): Stroke? {
    return state.value.rootRegion?.findDownestStroke()
}

//same logic with eraseActionDownHandle but with different argument
//does not store removed strokes for undo
fun eraseStrokeByFirstDot(dot: Dot, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var currentRemoveStroke = it.removedStrokes
        //if (!currentRemoveStroke.isEmpty()) currentRemoveStroke = emptyList()
        //remove oversize strokes
        val currentOversizeStrokes =
            removeOversizeStrokes(dot, false, state)
        //find strokes to remove
        currentRemoveStroke = currentRemoveStroke.plus(
            it.rootRegion!!.findStrokesToRemove(
                dot,
                currentRemoveStroke.toMutableList()
            ).toList()
        )
        //removing after getting all mapped strokes
        if (!currentRemoveStroke.isEmpty()) it.rootRegion!!.removeStrokes(
            currentRemoveStroke
        )
        it.copy(
            removedStrokes = emptyList(),
            oversizeStrokes = currentOversizeStrokes
        )
    }
}