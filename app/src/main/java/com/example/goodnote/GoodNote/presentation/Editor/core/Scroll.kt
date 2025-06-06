package com.example.goodnote.goodNote.presentation.editor.core

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.goodNote.action.ScrollAction
import com.example.goodnote.goodNote.domain.Image
import com.example.goodnote.domain.getDownest
import com.example.goodnote.domain.getRightest
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.goodNote.utils.AppConvertor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

// decide the direction of scrolling
fun scrollDirection(amount: Offset, previousPosition: Offset, state: MutableStateFlow<EditorState>) {
    var scrollDirection: ScrollAction = ScrollAction.NONE

    if (abs(amount.x) > abs(amount.y * AppConst.SCROLL_AXIS_DOMINANT) && abs(amount.x) >= AppConst.SCROLL_MINIMUM) {
        scrollDirection = if (amount.x > 0) ScrollAction.LEFT else ScrollAction.RIGHT
    } else if (abs(amount.y) > abs(amount.x * AppConst.SCROLL_AXIS_DOMINANT) && abs(amount.y) >= AppConst.SCROLL_MINIMUM) {
        scrollDirection = if (amount.y > 0) ScrollAction.UP else ScrollAction.DOWN
    } else if (amount.x > 0 && amount.x >= AppConst.SCROLL_MINIMUM) {
        if (amount.y > 0 && amount.y >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.LEFT_UP
        else if (amount.y < 0 && abs(amount.y) >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.LEFT_DOWN
        else scrollDirection = ScrollAction.LEFT
    } else if (amount.x < 0 && abs(amount.x) >= AppConst.SCROLL_MINIMUM) {
        if (amount.y > 0 && amount.y >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.RIGHT_UP
        else if (amount.y < 0 && abs(amount.y) >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.RIGHT_DOWN
        else scrollDirection = ScrollAction.RIGHT
    }
    processScrollDirection(scrollDirection, previousPosition, state)
}

//only allows to scroll to right + down when the remained space < MIN_SPACE
//prevent from creating too much unused space
private fun processScrollDirection(scrollDirection: ScrollAction, previousPosition: Offset, state: MutableStateFlow<EditorState>) {
    when (scrollDirection) {
        ScrollAction.RIGHT -> {
            if (
                state.value.canvasRelativePosition.x + state.value.screenWidth - (state.value.rightest?.getRightest()?.x
                    ?: 0f) <= AppConst.MIN_FREE_SPACE
            ) {
                moveStrokes(scrollDirection, previousPosition, state)
            }
        }

        ScrollAction.DOWN -> {
            if (
                state.value.canvasRelativePosition.y + state.value.screenHeight - (state.value.downest?.getDownest()?.y
                    ?: 0f) <= AppConst.MIN_FREE_SPACE
            ) {
                moveStrokes(scrollDirection, previousPosition, state)
            }
        }

        ScrollAction.RIGHT_DOWN -> {
            if (
                (
                        state.value.canvasRelativePosition.x + state.value.screenWidth - (state.value.rightest?.getRightest()?.x
                            ?: 0f) <= AppConst.MIN_FREE_SPACE
                        ) &&
                (
                        state.value.canvasRelativePosition.y + state.value.screenHeight - (state.value.downest?.getDownest()?.y
                            ?: 0f) <= AppConst.MIN_FREE_SPACE
                        )
            ) {
                moveStrokes(scrollDirection, previousPosition, state)
            }
        }

        else -> moveStrokes(scrollDirection, previousPosition, state)
    }
}

private fun moveStrokes(scrollAction: ScrollAction, previousPosition: Offset, state: MutableStateFlow<EditorState>) {
    when (scrollAction) {
        ScrollAction.RIGHT -> {
            addSizeToCanvas(Offset(-AppConst.SCROLL_LEVEL, 0f), state)
            moveVirtualCamera(Offset(-AppConst.SCROLL_LEVEL, 0f), previousPosition, state)
        }

        ScrollAction.LEFT -> {
            moveVirtualCamera(Offset(AppConst.SCROLL_LEVEL, 0f), previousPosition, state)
        }

        ScrollAction.UP -> {
            moveVirtualCamera(Offset(0f, AppConst.SCROLL_LEVEL), previousPosition, state)
        }

        ScrollAction.DOWN -> {
            addSizeToCanvas(Offset(0f, -AppConst.SCROLL_LEVEL), state)
            moveVirtualCamera(Offset(0f, -AppConst.SCROLL_LEVEL), previousPosition, state)
        }

        ScrollAction.RIGHT_UP -> {
            addSizeToCanvas(Offset(-AppConst.SCROLL_LEVEL, 0f), state)
            moveVirtualCamera(
                Offset(-AppConst.SCROLL_LEVEL, AppConst.SCROLL_LEVEL),
                previousPosition,
                state
            )
        }

        ScrollAction.RIGHT_DOWN -> {
            addSizeToCanvas(Offset(-AppConst.SCROLL_LEVEL, -AppConst.SCROLL_LEVEL), state)
            moveVirtualCamera(
                Offset(-AppConst.SCROLL_LEVEL, -AppConst.SCROLL_LEVEL),
                previousPosition,
                state
            )
        }

        ScrollAction.LEFT_UP -> {
            moveVirtualCamera(
                Offset(AppConst.SCROLL_LEVEL, AppConst.SCROLL_LEVEL),
                previousPosition,
                state
            )
        }

        ScrollAction.LEFT_DOWN -> {
            addSizeToCanvas(Offset(0f, -AppConst.SCROLL_LEVEL), state)
            moveVirtualCamera(
                Offset(AppConst.SCROLL_LEVEL, -AppConst.SCROLL_LEVEL),
                previousPosition,
                state
            )
        }

        ScrollAction.NONE -> {}
    }
}

private fun addSizeToCanvas(amount: Offset, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var newRegion = it.rootRegion?.addSize(AppConvertor.convertOffset(amount))
        it.copy(
            rootRegion = newRegion
        )
    }
    canvasFillScreen(state)
}

private fun moveVirtualCamera(amount: Offset, previousPosition: Offset, state: MutableStateFlow<EditorState>) {
    state.update { it ->
        var currentCanvasRelativePosition = it.canvasRelativePosition.copy(
            x = it.canvasRelativePosition.x - amount.x,
            y = it.canvasRelativePosition.y - amount.y
        )
        if (currentCanvasRelativePosition.x > 0 && currentCanvasRelativePosition.y > 0) {
            return@update it.copy(
                canvasRelativePosition = currentCanvasRelativePosition,
                scrollOffset = previousPosition
            )
        } else if (currentCanvasRelativePosition.x > 0) {
            return@update it.copy(
                canvasRelativePosition = currentCanvasRelativePosition.copy(y = it.canvasRelativePosition.y),
                scrollOffset = previousPosition
            )
        } else if (currentCanvasRelativePosition.y > 0) {
            return@update it.copy(
                canvasRelativePosition = currentCanvasRelativePosition.copy(x = it.canvasRelativePosition.x),
                scrollOffset = previousPosition
            )
        }
        it.copy()
    }
}

fun onImageScroll(scrollAction: ScrollAction, image: Image, scale: Float): Image {
    if (scrollAction == ScrollAction.RIGHT)
        image.left -= AppConst.SCROLL_LEVEL / scale
    else if (scrollAction == ScrollAction.LEFT)
        image.left += AppConst.SCROLL_LEVEL / scale
    else if (scrollAction == ScrollAction.DOWN)
        image.top -= AppConst.SCROLL_LEVEL / scale
    else if (scrollAction == ScrollAction.UP)
        image.top += AppConst.SCROLL_LEVEL / scale
    else if (scrollAction == ScrollAction.RIGHT_DOWN) {
        image.left -= AppConst.SCROLL_LEVEL / scale
        image.top -= AppConst.SCROLL_LEVEL / scale
    } else if (scrollAction == ScrollAction.LEFT_DOWN) {
        image.left += AppConst.SCROLL_LEVEL / scale
        image.top -= AppConst.SCROLL_LEVEL / scale
    } else if (scrollAction == ScrollAction.RIGHT_UP) {
        image.left -= AppConst.SCROLL_LEVEL / scale
        image.top += AppConst.SCROLL_LEVEL / scale
    } else if (scrollAction == ScrollAction.LEFT_UP) {
        image.left += AppConst.SCROLL_LEVEL / scale
        image.top += AppConst.SCROLL_LEVEL / scale
    }
    return image
}

fun imageScrollHandle(motionEvent: MotionEvent, state: MutableStateFlow<EditorState>, image: Image) {
    val action = motionEvent.actionMasked
    if (image.isSelected) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                state.update { it ->
                    it.copy(
                        scrollOffset = Offset(motionEvent.x, motionEvent.y)
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> {
                state.update { it ->
                    val moveDistance: Offset = Offset(
                        motionEvent.x - state.value.scrollOffset.x,
                        motionEvent.y - state.value.scrollOffset.y
                    )
                    val newImage: Image = onImageScroll(getScrollDirection(moveDistance), image, it.scale)
                        .setActualPosition(it.canvasRelativePosition, it.scale)
                    it.copy(
                        imageManager = it.imageManager.onScrolling(newImage),
                        scrollOffset = Offset(motionEvent.x, motionEvent.y),
                        lastMovingImage = newImage
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                state.update { it ->
                    it.copy(
                        lastMovingImage = null
                    )
                }
            }
        }
    }
}

//get motion direction
fun getScrollDirection(amount: Offset): ScrollAction {
    var scrollDirection: ScrollAction = ScrollAction.NONE

    if (abs(amount.x) > abs(amount.y * AppConst.SCROLL_AXIS_DOMINANT) && abs(amount.x) >= AppConst.SCROLL_MINIMUM) {
        scrollDirection = if (amount.x > 0) ScrollAction.LEFT else ScrollAction.RIGHT
    } else if (abs(amount.y) > abs(amount.x * AppConst.SCROLL_AXIS_DOMINANT) && abs(amount.y) >= AppConst.SCROLL_MINIMUM) {
        scrollDirection = if (amount.y > 0) ScrollAction.UP else ScrollAction.DOWN
    } else if (amount.x > 0 && amount.x >= AppConst.SCROLL_MINIMUM) {
        if (amount.y > 0 && amount.y >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.LEFT_UP
        else if (amount.y < 0 && abs(amount.y) >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.LEFT_DOWN
        else scrollDirection = ScrollAction.LEFT
    } else if (amount.x < 0 && abs(amount.x) >= AppConst.SCROLL_MINIMUM) {
        if (amount.y > 0 && amount.y >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.RIGHT_UP
        else if (amount.y < 0 && abs(amount.y) >= AppConst.SCROLL_MINIMUM) scrollDirection =
            ScrollAction.RIGHT_DOWN
        else scrollDirection = ScrollAction.RIGHT
    }
    return scrollDirection
}

fun scrollScreen(event: MotionEvent, index: Int, state: MutableStateFlow<EditorState>) {
    val action = event.actionMasked
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