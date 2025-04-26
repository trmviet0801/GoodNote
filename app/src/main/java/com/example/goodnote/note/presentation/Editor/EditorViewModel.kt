package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.note.action.InsertAction
import com.example.goodnote.note.action.ScrollAction
import com.example.goodnote.note.domain.Boundary
import com.example.goodnote.note.domain.Dot
import com.example.goodnote.note.domain.Region
import com.example.goodnote.note.domain.Stroke
import com.example.goodnote.note.domain.calActualSize
import com.example.goodnote.note.domain.contains
import com.example.goodnote.note.domain.getDownest
import com.example.goodnote.note.domain.getRightest
import com.example.goodnote.note.utils.AppConst
import com.example.goodnote.note.utils.AppConvertor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.collections.toList
import kotlin.math.abs

class EditorViewModel() : ViewModel() {
    private val _state = MutableStateFlow(EditorState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            EditorState()
        )

    fun adjustPageSize(layoutCoordinates: LayoutCoordinates) {
        if (_state.value.rootRegion == null) {
            val boundary = boundaryFromPositionAndSize(
                layoutCoordinates.positionOnScreen(),
                layoutCoordinates.size.width,
                layoutCoordinates.size.height
            )
            val region: Region = Region(boundary = boundary)
            region.isRoot = true
            _state.update { it ->
                it.copy(
                    rootRegion = region,
                    screenWidth = layoutCoordinates.size.width,
                    screenHeight = layoutCoordinates.size.height
                )
            }
        }
    }

    private fun boundaryFromPositionAndSize(position: Offset, width: Int, height: Int): Boundary {
        val x = position.x + width / 2
        val y = position.y + height / 2
        val w = width / 2
        val h = height / 2
        return Boundary(x, y, w.toFloat(), h.toFloat()).calActualSize()
    }

    fun handleInput(motionEvent: MotionEvent) {
        for (i in 0 until motionEvent.pointerCount) {
            when (motionEvent.getToolType(i)) {
                MotionEvent.TOOL_TYPE_STYLUS -> stylusHandle(motionEvent)
                MotionEvent.TOOL_TYPE_FINGER -> fingerHandle(motionEvent, i)
                else -> {}
            }
        }
    }

    private fun fingerHandle(event: MotionEvent, index: Int) {
        val action = event.actionMasked
        if (event.pointerCount == 1) {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    _state.update { it ->
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
                        event.getX(index) - _state.value.scrollOffset.x,
                        event.getY(index) - _state.value.scrollOffset.y
                    )
                    scroll(amountOffset, Offset(event.getX(index), event.getY(index)))
                }

                MotionEvent.ACTION_UP -> {
                    _state.update { it ->
                        it.copy(
                            scrollOffset = Offset(event.x, event.y)
                        )
                    }
                    true
                }
            }
        }
    }

    // decide the direction of scrolling
    private fun scroll(amount: Offset, previousPosition: Offset) {
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
        processScrollDirection(scrollDirection, previousPosition)
    }

    //only allows to scroll to right + down when the remained space < MIN_SPACE
    //prevent from creating too much unused space
    private fun processScrollDirection(scrollDirection: ScrollAction, previousPosition: Offset) {
        when (scrollDirection) {
            ScrollAction.RIGHT -> {
                if (
                    _state.value.canvasRelativePosition.x + _state.value.screenWidth - (_state.value.rightest?.getRightest()?.x
                        ?: 0f) <= AppConst.MIN_FREE_SPACE
                ) {
                    moveStrokes(scrollDirection, previousPosition)
                }
            }

            ScrollAction.DOWN -> {
                if (
                    _state.value.canvasRelativePosition.y + _state.value.screenHeight - (_state.value.downest?.getDownest()?.y
                        ?: 0f) <= AppConst.MIN_FREE_SPACE
                ) {
                    moveStrokes(scrollDirection, previousPosition)
                } else {
                    Log.d(
                        "erase",
                        "Downest: ${_state.value.downest?.getDownest()?.y} = canvas height: ${_state.value.rootRegion?.boundary?.actualHeight}"
                    )
                }
            }

            ScrollAction.RIGHT_DOWN -> {
                if (
                    (
                            _state.value.canvasRelativePosition.x + _state.value.screenWidth - (_state.value.rightest?.getRightest()?.x
                                ?: 0f) <= AppConst.MIN_FREE_SPACE
                            ) &&
                    (
                            _state.value.canvasRelativePosition.y + _state.value.screenHeight - (_state.value.downest?.getDownest()?.y
                                ?: 0f) <= AppConst.MIN_FREE_SPACE
                            )
                ) {
                    moveStrokes(scrollDirection, previousPosition)
                }
            }

            else -> moveStrokes(scrollDirection, previousPosition)
        }
    }

    private fun addSizeToCanvas(amount: Offset) {
        _state.update { it ->
            var newRegion = it.rootRegion?.addSize(AppConvertor.convertOffset(amount))
            it.copy(
                rootRegion = newRegion
            )
        }
        canvasFillScreen()
    }

    private fun canvasFillScreen() {
        val extraX =
            _state.value.screenWidth -
                    (_state.value.rootRegion!!.boundary!!.actualWidth * _state.value.scale)
        val extraY =
            _state.value.screenHeight -
                    (_state.value.rootRegion!!.boundary!!.actualHeight * _state.value.scale)
        _state.update { it ->
            if (extraX > 0 && extraY > 0) {
                val newRegion = it.rootRegion?.addSize(
                    Offset(
                        extraX / _state.value.scale,
                        extraY / _state.value.scale
                    )
                )
                it.copy(
                    rootRegion = newRegion
                )
            } else if (extraX > 0 && extraY <= 0) {
                val newRegion = it.rootRegion?.addSize(Offset(extraX / _state.value.scale, 0f))
                it.copy(
                    rootRegion = newRegion
                )
            } else if (extraX < 0 && extraY > 0) {
                val newRegion = it.rootRegion?.addSize(Offset(0f, extraY / _state.value.scale))
                it.copy(
                    rootRegion = newRegion
                )
            } else {
                it.copy()
            }
        }
    }

    //update rightest stroke after erasing for keeping scrolling behavior
    // if erased stroke was the rightest
    private fun findRightestStroke(): Stroke? {
        return _state.value.rootRegion?.findRightestStroke()
    }

    private fun findDownestStroke(): Stroke? {
        return _state.value.rootRegion?.findDownestStroke()
    }

    //checking if need to update rightest stroke or downest stroke
    //if need -> update
    private fun updateFarestStrokes() {
        var rightestStroke: Stroke? = null
        var downestStroke: Stroke? = null
        if (_state.value.rightest == null || _state.value.rightest?.dots?.isEmpty() == true)
            rightestStroke = findRightestStroke()
        if (_state.value.downest == null || _state.value.downest?.dots?.isEmpty() == true)
            downestStroke = findDownestStroke()
        _state.update { it ->
            it.copy(
                rightest = rightestStroke ?: it.rightest,
                downest = downestStroke ?: it.downest
            )
        }
    }

    private fun moveVirtualCamera(amount: Offset, previousPosition: Offset) {
        _state.update { it ->
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

    private fun moveStrokes(scrollAction: ScrollAction, previousPosition: Offset) {
        when (scrollAction) {
            ScrollAction.RIGHT -> {
                addSizeToCanvas(Offset(-AppConst.SCROLL_LEVEL, 0f))
                moveVirtualCamera(Offset(-AppConst.SCROLL_LEVEL, 0f), previousPosition)
            }

            ScrollAction.LEFT -> {
                moveVirtualCamera(Offset(AppConst.SCROLL_LEVEL, 0f), previousPosition)
            }

            ScrollAction.UP -> {
                moveVirtualCamera(Offset(0f, AppConst.SCROLL_LEVEL), previousPosition)
            }

            ScrollAction.DOWN -> {
                addSizeToCanvas(Offset(0f, -AppConst.SCROLL_LEVEL))
                moveVirtualCamera(Offset(0f, -AppConst.SCROLL_LEVEL), previousPosition)
            }

            ScrollAction.RIGHT_UP -> {
                addSizeToCanvas(Offset(-AppConst.SCROLL_LEVEL, 0f))
                moveVirtualCamera(
                    Offset(-AppConst.SCROLL_LEVEL, AppConst.SCROLL_LEVEL),
                    previousPosition
                )
            }

            ScrollAction.RIGHT_DOWN -> {
                addSizeToCanvas(Offset(-AppConst.SCROLL_LEVEL, -AppConst.SCROLL_LEVEL))
                moveVirtualCamera(
                    Offset(-AppConst.SCROLL_LEVEL, -AppConst.SCROLL_LEVEL),
                    previousPosition
                )
            }

            ScrollAction.LEFT_UP -> {
                moveVirtualCamera(
                    Offset(AppConst.SCROLL_LEVEL, AppConst.SCROLL_LEVEL),
                    previousPosition
                )
            }

            ScrollAction.LEFT_DOWN -> {
                addSizeToCanvas(Offset(0f, -AppConst.SCROLL_LEVEL))
                moveVirtualCamera(
                    Offset(AppConst.SCROLL_LEVEL, -AppConst.SCROLL_LEVEL),
                    previousPosition
                )
            }

            ScrollAction.NONE -> {}
        }
    }

    private fun stylusWritingHandle(motionEvent: MotionEvent) {
        val action = motionEvent.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                _state.update { it ->
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

            MotionEvent.ACTION_MOVE -> {
                _state.update { it ->
                    val currentStroke = it.latestStroke
                    val currentDots = currentStroke.dots.toMutableList()
                    currentDots.add(
                        convertMotionEventToDot(motionEvent)
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

            MotionEvent.ACTION_UP -> {
                _state.update { it ->
                    var currentLatestStroke = it.latestStroke
                    val currentRootRegion = it.rootRegion
                    //update the farest stroke in the root region
                    var currentRightest: Stroke =
                        if (isRightest(currentLatestStroke)) {
                            if (it.rightest !== null)
                                it.rightest!!.isRightest = false
                            currentLatestStroke.isRightest = true
                            currentLatestStroke
                        } else it.rightest
                            ?: Stroke()
                    var currentDownest: Stroke =
                        if (isDownest(currentLatestStroke)) {
                            if (it.downest !== null)
                                it.downest!!.isDownest = false
                            currentLatestStroke.isDownest = true
                            currentLatestStroke
                        } else it.downest
                            ?: Stroke()

                    var currentOversizeStroke: List<Stroke> = it.oversizeStrokes

                    // insert new stroke into root region
                    // if stroke is oversize -> add to oversize list for erasing behavior
                    if (currentRootRegion?.insert(currentLatestStroke) == InsertAction.Oversize)
                        currentOversizeStroke = currentOversizeStroke.plus(currentLatestStroke)

                    currentLatestStroke = Stroke()

                    it.copy(
                        latestStroke = currentLatestStroke,
                        rootRegion = currentRootRegion,
                        rightest = currentRightest,
                        downest = currentDownest,
                        oversizeStrokes = currentOversizeStroke
                    )
                }
            }
        }
    }

    private fun stylusHandle(motionEvent: MotionEvent) {
        when (motionEvent.buttonState) {
            0 -> stylusWritingHandle(motionEvent)
            32 -> eraseHandle(motionEvent)
        }
    }

    private fun eraseHandle(motionEvent: MotionEvent) {
        val action = motionEvent.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                _state.update { it ->
                    var currentRemoveStroke = it.removedStrokes
                    if (!currentRemoveStroke.isEmpty()) currentRemoveStroke = emptyList()
                    val currentOversizeStrokes =
                        removeOversizeStrokes(convertMotionEventToDot(motionEvent))
                    currentRemoveStroke = currentRemoveStroke.plus(
                        it.rootRegion!!.findStrokesToRemove(
                            convertMotionEventToDot(motionEvent),
                            currentRemoveStroke.toMutableList()
                        ).toList()
                    )
                    if (!currentRemoveStroke.isEmpty()) it.rootRegion!!.removeStrokes(
                        currentRemoveStroke
                    )
                    it.copy(
                        removedStrokes = currentRemoveStroke,
                        oversizeStrokes = currentOversizeStrokes
                    )
                }
            }

            MotionEvent.ACTION_MOVE -> {
                _state.update { it ->
                    var currentRemoveStroke = it.removedStrokes
                    val currentOversizeStrokes =
                        removeOversizeStrokes(convertMotionEventToDot(motionEvent))
                    currentRemoveStroke = currentRemoveStroke.plus(
                        it.rootRegion!!.findStrokesToRemove(
                            convertMotionEventToDot(motionEvent),
                            currentRemoveStroke.toMutableList()
                        ).toList()
                    )
                    if (!currentRemoveStroke.isEmpty()) it.rootRegion!!.removeStrokes(
                        currentRemoveStroke
                    )
                    it.copy(
                        removedStrokes = currentRemoveStroke,
                        oversizeStrokes = currentOversizeStrokes
                    )
                }
            }

            MotionEvent.ACTION_UP -> {
                _state.update { it ->
                    it.copy(
                        removedStrokes = emptyList<Stroke>()
                    )
                }
            }
        }
        updateFarestStrokes()
        Log.d(
            "erase",
            "updateFarestStrokes: rightestStroke: ${_state.value.rightest?.getRightest()?.x} downestStroke: ${_state.value.downest?.getDownest()?.y}"
        )
    }

    // / scale make the position is the offset with scale = 1 (strokes are stored with scale = 1 too)
    // x,y always the position with scale = 1f
    // scaledX, scaledY are the position with the current scale
    private fun convertMotionEventToDot(motionEvent: MotionEvent): Dot {
        return Dot(
            (motionEvent.x + _state.value.canvasRelativePosition.x) / _state.value.scale,
            (motionEvent.y + _state.value.canvasRelativePosition.y) / _state.value.scale,
            motionEvent.x + _state.value.canvasRelativePosition.x,
            motionEvent.y + _state.value.canvasRelativePosition.y
        )
    }

    //change dots to empty
    //remove in oversize list
    private fun removeOversizeStrokes(dot: Dot): List<Stroke> {
        var result = mutableListOf<Stroke>()
        var currentOversizeStrokes = _state.value.oversizeStrokes

        currentOversizeStrokes.forEach { stroke ->
            if (stroke.contains(dot)) {
                stroke.dots = emptyList<Dot>()
            } else {
                result.add(stroke)
            }
        }
        return result.toList()
    }

    private fun isRightest(stroke: Stroke): Boolean {
        if (_state.value.rightest == null || _state?.value?.rightest?.dots == emptyList<Dot>()) return true
        return stroke.getRightest()!!.x > _state.value.rightest!!.getRightest()!!.x
    }

    private fun isDownest(stroke: Stroke): Boolean {
        if (_state.value.downest == null || _state?.value?.downest?.dots == emptyList<Dot>()) return true
        return stroke.getDownest()!!.y > _state.value.downest!!.getDownest()!!.y
    }

    private fun adjustScale(isIncrease: Boolean) {
        _state.update { it ->
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
        canvasFillScreen()
    }

    fun scaling(detector: ScaleGestureDetector) {
        val scaleFactor = detector.scaleFactor
        if (scaleFactor == 1f) return
        //zoom in
        if (scaleFactor > 1f) adjustScale(true) else adjustScale(false)
    }

    //change file name
    fun onTitleChange(newFileName: String) {
        _state.update { it ->
            it.copy(
                name = newFileName
            )
        }
    }
}