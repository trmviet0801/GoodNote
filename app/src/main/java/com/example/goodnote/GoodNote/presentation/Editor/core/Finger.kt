package com.example.goodnote.goodNote.presentation.editor.core

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.goodNote.domain.Image
import com.example.goodnote.goodNote.presentation.editor.EditorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun fingerHandle(event: MotionEvent, index: Int, state: MutableStateFlow<EditorState>) {
    val action = event.actionMasked
    if (event.pointerCount == 1) {
        val image: Image? = state.value.imageManager.isTouchOnImage(
            Offset(event.x, event.y),
            state.value.canvasRelativePosition,
            state.value.scale
        )
        if (image == null)
            scrollScreen(event, index, state)
        else
            imageScrollHandle(event, state, image)
    }
}

