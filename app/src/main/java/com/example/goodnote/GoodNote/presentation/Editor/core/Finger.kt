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
        val lastMovingImage: Image? = state.value.lastMovingImage
        if (image != null)
            imageScrollHandle(event, state, image)
        else if (lastMovingImage != null && lastMovingImage.isSelected)
            imageScrollHandle(event, state, lastMovingImage)
        else
            scrollScreen(event, index, state)
    }
}

