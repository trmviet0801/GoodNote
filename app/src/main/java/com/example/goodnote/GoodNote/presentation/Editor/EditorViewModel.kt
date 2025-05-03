package com.example.goodnote.goodNote.presentation.editor

import android.util.Log
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.goodNote.action.InsertAction
import com.example.goodnote.goodNote.action.ScrollAction
import com.example.goodnote.goodNote.action.StrokeAction
import com.example.goodnote.goodNote.domain.Boundary
import com.example.goodnote.goodNote.domain.Dot
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.domain.Stroke
import com.example.goodnote.goodNote.domain.calActualSize
import com.example.goodnote.goodNote.domain.contains
import com.example.goodnote.goodNote.domain.getDownest
import com.example.goodnote.goodNote.domain.getRightest
import com.example.goodnote.goodNote.presentation.model.StrokeBehavior
import com.example.goodnote.goodNote.presentation.model.popBehavior
import com.example.goodnote.goodNote.presentation.model.pushBehavior
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.goodNote.utils.AppConvertor
import com.example.goodnote.goodNote.utils.PenConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.collections.toList
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

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

    fun onDisplayPenWidthSelection() {
        _state.update { it ->
            it.copy(
                isShowPenPicker = !it.isShowPenPicker
            )
        }
    }

    fun onDisplayColorPicker() {
        _state.update { it ->
            it.copy(
                isShowColorPicker = !it.isShowColorPicker
            )
        }
    }

    // calculating scale factor based on distance between fingers
    fun onScaleChange(motionEvent: MotionEvent) {
        val distance = distanceBetweenFingers(
            Offset(motionEvent.getX(0), motionEvent.getY(0)),
            Offset(motionEvent.getX(1), motionEvent.getY(1))
        )
        var currentScaleFactor = _state.value.scaleFactor
        if (currentScaleFactor != 0f) {
            if (distance != currentScaleFactor) {
                // scaling the canvas
                scaling(distance / currentScaleFactor)
                _state.update { it ->
                    it.copy(
                        scaleFactor = distance
                    )
                }
            }
        } else {
            _state.update { it ->
                it.copy(
                    scaleFactor = distance
                )
            }
        }
    }

    private fun distanceBetweenFingers(x: Offset, y: Offset): Float {
        return sqrt((x.x - y.x).pow(2) + (x.y - y.y).pow(2))
    }

    fun handleInput(motionEvent: MotionEvent) {
        //scale detector
        if (motionEvent.pointerCount == 2 &&
            motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER &&
            motionEvent.getToolType(1) == MotionEvent.TOOL_TYPE_FINGER
        ) {
            onScaleChange(motionEvent)
        } else {
            for (i in 0 until motionEvent.pointerCount) {
                when (motionEvent.getToolType(i)) {
                    MotionEvent.TOOL_TYPE_STYLUS -> stylusHandle(motionEvent)
                    MotionEvent.TOOL_TYPE_FINGER -> fingerHandle(motionEvent, i)
                    else -> {}
                }
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
                    scrollDirection(amountOffset, Offset(event.getX(index), event.getY(index)))
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
    private fun scrollDirection(amount: Offset, previousPosition: Offset) {
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
            MotionEvent.ACTION_DOWN -> stylusWritingActionDownHandle()

            MotionEvent.ACTION_MOVE -> stylusWritingActionMoveHandle(motionEvent)

            MotionEvent.ACTION_UP -> stylusWritingActionUpHandle()
        }
    }

    //set up the up-coming stroke (empty dots, color, width)
    private fun stylusWritingActionDownHandle() {
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

    private fun stylusWritingActionMoveHandle(motionEvent: MotionEvent) {
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

    // storing new stroke to display in the canvas
    // stored stoke is _state.latestStroke
    private fun stylusWritingActionUpHandle(isUndo: Boolean = false) {
        _state.update { it ->
            var currentOversizeStroke: List<Stroke> = it.oversizeStrokes
            var currentLatestStroke = it.latestStroke
            val currentRootRegion = it.rootRegion
            //update the farest stroke in the root region
            var currentRightest: Stroke = getCurrentRightest(currentLatestStroke)
            var currentDownest: Stroke = getCurrentDownest(currentLatestStroke)
            //update new action for undo/redo
            if (!isUndo) storeNewStroke(currentLatestStroke)
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

    private fun getCurrentDownest(stroke: Stroke): Stroke {
        return if (isDownest(stroke)) {
            if (_state.value.downest !== null)
                _state.value.downest!!.isDownest = false
            stroke.isDownest = true
            stroke
        } else _state.value.downest
            ?: Stroke()
    }

    private fun getCurrentRightest(stroke: Stroke): Stroke {
        return if (isRightest(stroke)) {
            if (_state.value.rightest !== null)
                _state.value.rightest!!.isRightest = false
            stroke.isRightest = true
            stroke
        } else _state.value.rightest
            ?: Stroke()
    }

    private fun stylusHandle(motionEvent: MotionEvent) {
        when (motionEvent.buttonState) {
            0 -> if (!_state.value.isEraser) stylusWritingHandle(motionEvent) else eraseHandle(
                motionEvent
            )

            32 -> eraseHandle(motionEvent)
        }
    }

    private fun eraseActionDownHandle(motionEvent: MotionEvent) {
        _state.update { it ->
            var currentRemoveStroke = it.removedStrokes
            if (!currentRemoveStroke.isEmpty()) currentRemoveStroke = emptyList()
            //remove oversize strokes
            val currentOversizeStrokes =
                removeOversizeStrokes(convertMotionEventToDot(motionEvent))
            //find strokes to remove
            currentRemoveStroke = currentRemoveStroke.plus(
                it.rootRegion!!.findStrokesToRemove(
                    convertMotionEventToDot(motionEvent),
                    currentRemoveStroke.toMutableList()
                ).toList()
            )
            //storing removed strokes for undo/redo
            currentRemoveStroke.forEach { stroke -> storeErasedStroke(stroke) }

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

    //same logic with eraseActionDownHandle but with different argument
    // and does not store removed strokes for undo
    private fun eraseStrokeByFirstDot(dot: Dot) {
        _state.update { it ->
            var currentRemoveStroke = it.removedStrokes
            //if (!currentRemoveStroke.isEmpty()) currentRemoveStroke = emptyList()
            //remove oversize strokes
            val currentOversizeStrokes =
                removeOversizeStrokes(dot, false)
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

    private fun eraseActionMoveHandle(motionEvent: MotionEvent) {
        _state.update { it ->
            var currentRemoveStroke = it.removedStrokes
            //remove oversize strokes
            val currentOversizeStrokes =
                removeOversizeStrokes(convertMotionEventToDot(motionEvent))
            currentRemoveStroke = currentRemoveStroke.plus(
                it.rootRegion!!.findStrokesToRemove(
                    convertMotionEventToDot(motionEvent),
                    currentRemoveStroke.toMutableList()
                ).toList()
            )
            //storing removed strokes for undo/redo
//            currentRemoveStroke.forEach { stroke ->
//                storeErasedStroke(stroke)
//                Log.d("scrolll", "add in 2st")
//            }
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

    private fun eraseActionUpHandle() {
        _state.update { it ->
            it.copy(
                removedStrokes = emptyList<Stroke>()
            )
        }
    }

    private fun eraseHandle(motionEvent: MotionEvent) {
        val action = motionEvent.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> eraseActionDownHandle(motionEvent)
            MotionEvent.ACTION_MOVE -> eraseActionMoveHandle(motionEvent)
            MotionEvent.ACTION_UP -> eraseActionUpHandle()
        }
        updateFarestStrokes()
    }

    // scale make the position is the offset with scale = 1 (strokes are stored with scale = 1 too)
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
    //remove in oversize list + store removed strokes for undo/redo
    private fun removeOversizeStrokes(dot: Dot, isStoreUndo: Boolean = true): List<Stroke> {
        var result = mutableListOf<Stroke>()
        var currentOversizeStrokes = _state.value.oversizeStrokes

        currentOversizeStrokes.forEach { stroke ->
            if (stroke.contains(dot)) {
                if (isStoreUndo) storeErasedStroke(stroke)
                stroke.dots = emptyList<Dot>()
            } else {
                result.add(stroke)
            }
        }
        return result.toList()
    }

    private fun isRightest(stroke: Stroke): Boolean {
        if (_state.value.rightest == null || _state.value?.rightest?.dots == emptyList<Dot>()) return true
        return stroke.getRightest()!!.x > _state.value.rightest!!.getRightest()!!.x
    }

    private fun isDownest(stroke: Stroke): Boolean {
        if (_state.value.downest == null || _state.value?.downest?.dots == emptyList<Dot>()) return true
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

    fun scaling(scaleFactor: Float) {
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

    fun onFullScreenChange() {
        _state.update { it ->
            it.copy(
                isFullScreen = !it.isFullScreen
            )
        }
    }

    fun onPenColorChange(color: Long) {
        _state.update { it ->
            it.copy(
                color = color
            )
        }
    }

    fun onSavedColorChange(color: Long, index: Int) {
        _state.update { it ->
            var currentColors = it.savedColors.toMutableList()
            currentColors[index] = color
            val newIndex = if (index == 2) 0 else index + 1
            it.copy(
                savedColors = currentColors.toList(),
                currentSavedColorIndex = newIndex
            )
        }
    }

    //takes input as motion event of motion on pen width slice card
    fun handlePenSelectionTouch(motionEvent: MotionEvent) {
        if (motionEvent.pointerCount == 1) {
            var action = motionEvent.actionMasked
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    _state.update { it ->
                        it.copy(
                            penWidthScrollOffset = Offset(
                                motionEvent.x,
                                motionEvent.y
                            )
                        )
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val amountOffset = Offset(
                        motionEvent.x - _state.value.penWidthScrollOffset.x,
                        motionEvent.y - _state.value.penWidthScrollOffset.y
                    )
                    //scrolling
                    onPenWidthChange(
                        getPenSelectionDirection(amountOffset, Offset(motionEvent.x, motionEvent.y))
                    )
                }

                MotionEvent.ACTION_UP -> {
                    _state.update { it ->
                        it.copy(
                            penWidthScrollOffset = Offset(motionEvent.x, motionEvent.y)
                        )
                    }
                    true
                }
            }
        }
    }

    //get motion direction
    private fun getPenSelectionDirection(amount: Offset, previousPosition: Offset): ScrollAction {
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

    // scroll handle
    // only accept forward and backward scrolling
    fun onPenWidthChange(scrollAction: ScrollAction) {
        when (scrollAction) {
            //decrease pen width
            ScrollAction.RIGHT -> {
                _state.update { it ->
                    var currentLineWidthLevel = it.lineWidthLevel.toFloat()
                    currentLineWidthLevel =
                        if (currentLineWidthLevel.roundToInt() - 1 >= AppConst.PEN_WIDTH_LEVEL_MIN) currentLineWidthLevel - AppConst.PEN_WIDTH_MOVE_UNIT else currentLineWidthLevel
                    it.copy(
                        lineWidthLevel = currentLineWidthLevel,
                        lineWidth = PenConst.DEFAULT_LINE_WIDTH * currentLineWidthLevel
                    )
                }
            }
            //increase pen width
            ScrollAction.LEFT -> {
                _state.update { it ->
                    var currentLineWidthLevel = it.lineWidthLevel.toFloat()
                    currentLineWidthLevel =
                        if (currentLineWidthLevel.roundToInt() + 1 <= AppConst.PEN_WIDTH_LEVEL_MAX) currentLineWidthLevel + AppConst.PEN_WIDTH_MOVE_UNIT else currentLineWidthLevel
                    it.copy(
                        lineWidthLevel = currentLineWidthLevel,
                        lineWidth = PenConst.DEFAULT_LINE_WIDTH * currentLineWidthLevel
                    )
                }
            }

            else -> {}
        }
    }

    fun onEraser() {
        _state.update { it ->
            it.copy(
                isEraser = !it.isEraser
            )
        }
    }

    // triggers when using button instead of scrolling
    fun onPenWidthChange(scrollAction: ScrollAction, amount: Float) {
        when (scrollAction) {
            //decrease pen width
            ScrollAction.RIGHT -> {
                _state.update { it ->
                    var currentLineWidthLevel = it.lineWidthLevel.toFloat()
                    currentLineWidthLevel =
                        if (currentLineWidthLevel.roundToInt() - 1 >= AppConst.PEN_WIDTH_LEVEL_MIN) currentLineWidthLevel - amount else currentLineWidthLevel
                    it.copy(
                        lineWidthLevel = currentLineWidthLevel.roundToInt().toFloat(),
                        lineWidth = PenConst.DEFAULT_LINE_WIDTH * currentLineWidthLevel
                    )
                }
            }
            //increase pen width
            ScrollAction.LEFT -> {
                _state.update { it ->
                    var currentLineWidthLevel = it.lineWidthLevel.toFloat()
                    currentLineWidthLevel =
                        if (currentLineWidthLevel.roundToInt() + 1 <= AppConst.PEN_WIDTH_LEVEL_MAX) currentLineWidthLevel + amount else currentLineWidthLevel
                    Log.d("scrolll", "[+]onPenWidthChange: $currentLineWidthLevel")
                    it.copy(
                        lineWidthLevel = currentLineWidthLevel.roundToInt().toFloat(),
                        lineWidth = PenConst.DEFAULT_LINE_WIDTH * currentLineWidthLevel
                    )
                }
            }

            else -> {}
        }
    }

    private fun storeNewStroke(stroke: Stroke) {
        _state.value.undoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.WRITE, stroke.copy()))
    }

    private fun storeErasedStroke(stroke: Stroke) {
        _state.value.undoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.ERASE, stroke.copy()))
    }

    private fun storeRedoStroke(stroke: Stroke, isWrite: Boolean) {
        if (isWrite)
            _state.value.redoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.WRITE, stroke.copy()))
        else
            _state.value.redoStrokeBehaviors.pushBehavior(StrokeBehavior(StrokeAction.ERASE, stroke.copy()))
    }

    fun onUndo() {
        val lastBehavior: StrokeBehavior? = _state.value.undoStrokeBehaviors.popBehavior()
        when (lastBehavior?.action) {
            StrokeAction.WRITE -> undoWriteHandle(lastBehavior)
            StrokeAction.ERASE -> undoEraseHandle(lastBehavior)
            null -> {}
        }
    }

    fun onRedo() {
        val lastBehavior: StrokeBehavior? = _state.value.redoStrokeBehaviors.popBehavior()
        when (lastBehavior?.action) {
            StrokeAction.WRITE -> redoWriteHandle(lastBehavior)
            StrokeAction.ERASE -> redoEraseHandle(lastBehavior)
            null -> {}
        }
    }

    //erase stroke
    private fun redoWriteHandle(strokeBehavior: StrokeBehavior?) {
        if (strokeBehavior != null) {
            eraseStrokeByFirstDot(strokeBehavior.stroke.dots[0])
        }
    }

    //re-write stroke
    private fun redoEraseHandle(strokeBehavior: StrokeBehavior?) {
        if (strokeBehavior != null) {
            _state.value.latestStroke = strokeBehavior.stroke
            stylusWritingActionUpHandle(true)
        }
    }

    //erase new-write stroke
    private fun undoWriteHandle(strokeBehavior: StrokeBehavior?) {
        if (strokeBehavior != null) {
            eraseStrokeByFirstDot(strokeBehavior.stroke.dots[0])
            storeRedoStroke(strokeBehavior.stroke, false)
        }
    }

    //re-write erased stroke
    private fun undoEraseHandle(strokeBehavior: StrokeBehavior?) {
        if (strokeBehavior != null) {
            _state.value.latestStroke = strokeBehavior.stroke
            stylusWritingActionUpHandle(true)
            storeRedoStroke(strokeBehavior.stroke, true)
        }
    }
}