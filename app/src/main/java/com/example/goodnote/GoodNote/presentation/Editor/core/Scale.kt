package com.example.goodnote.goodNote.presentation.editor.core

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.utils.AppConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow
import kotlin.math.sqrt

// calculating scale factor based on distance between fingers
fun onScaleChange(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>) {
    val distance = distanceBetweenFingers(
        Offset(motionEvent.getX(0), motionEvent.getY(0)),
        Offset(motionEvent.getX(1), motionEvent.getY(1))
    )
    var currentScaleFactor = state.value.scaleFactor
    if (currentScaleFactor != 0f) {
        if (distance != currentScaleFactor) {
            // scaling the canvas
            scaling(distance / currentScaleFactor, state)
            state.update { it ->
                it.copy(
                    scaleFactor = distance
                )
            }
        }
    } else {
        state.update { it ->
            it.copy(
                scaleFactor = distance
            )
        }
    }
}

private fun distanceBetweenFingers(x: Offset, y: Offset): Float {
    return sqrt((x.x - y.x).pow(2) + (x.y - y.y).pow(2))
}

private fun scaling(scaleFactor: Float, state: MutableStateFlow<EditorState>) {
    if (scaleFactor == 1f) return
    //zoom in
    if (scaleFactor > 1f) adjustScale(true, state) else adjustScale(false, state)
}

fun adjustScale(isIncrease: Boolean, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var currentScale = it.scale
        if (isIncrease) currentScale += AppConst.SCALE_LEVEL else currentScale -= AppConst.SCALE_LEVEL
        val newRootRegion = it.rootRegion!!.scaleStrokes(currentScale)
        if (currentScale > 0.07) {
            it.copy(
                scale = currentScale,
                rootRegion = newRootRegion
            )
        } else {
            it
        }
    }
    canvasFillScreen(state)
}

fun canvasFillScreen(state: MutableStateFlow<EditorState>) {
    val extraX =
        state.value.screenWidth -
                (state.value.rootRegion!!.boundary!!.actualWidth * state.value.scale)
    val extraY =
        state.value.screenHeight -
                (state.value.rootRegion!!.boundary!!.actualHeight * state.value.scale)
    state.update { it ->
        if (extraX > 0 && extraY > 0) {
            val newRegion = it.rootRegion?.addSize(
                Offset(
                    extraX / state.value.scale,
                    extraY / state.value.scale
                )
            )
            it.copy(
                rootRegion = newRegion
            )
        } else if (extraX > 0 && extraY <= 0) {
            val newRegion = it.rootRegion?.addSize(Offset(extraX / state.value.scale, 0f))
            it.copy(
                rootRegion = newRegion
            )
        } else if (extraX < 0 && extraY > 0) {
            val newRegion = it.rootRegion?.addSize(Offset(0f, extraY / state.value.scale))
            it.copy(
                rootRegion = newRegion
            )
        } else {
            it.copy()
        }
    }
}