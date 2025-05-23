package com.example.goodnote.goodNote.presentation.editor.core

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.goodNote.presentation.editor.EditorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun fingerHandle(event: MotionEvent, index: Int, state: MutableStateFlow<EditorState>) {
    val action = event.actionMasked
    if (event.pointerCount == 1) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                state.update { it ->
                    it.copy(
                        scrollOffset = Offset(
                            event.getX(index),
                            event.getY(index)
                        )
                    )
                }
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val amountOffset = Offset(
                    event.getX(index) - state.value.scrollOffset.x,
                    event.getY(index) - state.value.scrollOffset.y
                )
                scrollDirection(amountOffset, Offset(event.getX(index), event.getY(index)), state)
            }

            MotionEvent.ACTION_UP -> {
                state.update { it ->
                    it.copy(
                        scrollOffset = Offset(event.x, event.y)
                    )
                }
                true
            }
        }
    }
}