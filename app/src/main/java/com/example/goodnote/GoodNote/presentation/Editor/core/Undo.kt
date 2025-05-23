package com.example.goodnote.goodNote.presentation.editor.core

import com.example.goodnote.goodNote.action.StrokeAction
import com.example.goodnote.goodNote.domain.Stroke
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.model.StrokeBehavior
import com.example.goodnote.goodNote.presentation.model.pushBehavior
import kotlinx.coroutines.flow.MutableStateFlow

//erase new-write stroke
fun undoWriteHandle(strokeBehavior: StrokeBehavior?, state: MutableStateFlow<EditorState>) {
    if (strokeBehavior != null) {
        eraseStrokeByFirstDot(strokeBehavior.stroke.dots[0], state)
        storeRedoStroke(strokeBehavior.stroke, false, state)
    }
}

//re-write erased stroke
fun undoEraseHandle(strokeBehavior: StrokeBehavior?, state: MutableStateFlow<EditorState>) {
    if (strokeBehavior != null) {
        state.value.latestStroke = strokeBehavior.stroke
        stylusWritingActionUpHandle(true, state)
        storeRedoStroke(strokeBehavior.stroke, true, state)
    }
}

private fun storeRedoStroke(stroke: Stroke, isWrite: Boolean, state: MutableStateFlow<EditorState>) {
    if (isWrite)
        state.value.redoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.WRITE, stroke.copy()))
    else
        state.value.redoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.ERASE, stroke.copy()))
}