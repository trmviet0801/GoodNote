package com.example.goodnote.goodNote.presentation.editor

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionOnScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.domain.Page
import com.example.goodnote.goodNote.action.ScrollAction
import com.example.goodnote.goodNote.action.StrokeAction
import com.example.goodnote.goodNote.assemblers.pageToState
import com.example.goodnote.goodNote.domain.Boundary
import com.example.goodnote.goodNote.domain.Image
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.domain.calActualSize
import com.example.goodnote.goodNote.presentation.editor.core.fingerHandle
import com.example.goodnote.goodNote.presentation.editor.core.getScrollDirection
import com.example.goodnote.goodNote.presentation.editor.core.onImageScroll
import com.example.goodnote.goodNote.presentation.editor.core.onScaleChange
import com.example.goodnote.goodNote.presentation.editor.core.stylusHandle
import com.example.goodnote.goodNote.presentation.editor.core.undoEraseHandle
import com.example.goodnote.goodNote.presentation.editor.core.undoWriteHandle
import com.example.goodnote.goodNote.presentation.editor.repository.ImageRepository
import com.example.goodnote.goodNote.presentation.editor.usecase.updateCurrentPage
import com.example.goodnote.goodNote.presentation.model.StrokeBehavior
import com.example.goodnote.goodNote.presentation.model.popBehavior
import com.example.goodnote.goodNote.repository.PageRepository
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.goodNote.utils.PenConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import redoEraseHandle
import redoWriteHandle
import kotlin.collections.toList
import kotlin.math.abs
import kotlin.math.roundToInt

class EditorViewModel(
    private val imageRepository: ImageRepository,
    private val pageRepository: PageRepository,
    private val regionRepository: RegionRepository,
    private val strokeRepository: StrokeRepository
) : ViewModel() {
    private val _state = MutableStateFlow(EditorState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            EditorState()
        )

    suspend fun loadState(pageId: String) {
        val page: Page = pageRepository.selectPageWithId(pageId).first() ?: EditorState().toPage()
        _state.value = pageToState(page, regionRepository, strokeRepository)
    }

    //update page + region + state
    fun updatePage() {
        viewModelScope.launch {
            updateCurrentPage(
                _state.value,
                pageRepository,
                regionRepository,
                strokeRepository
            )
        }
    }

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

    fun handleInput(motionEvent: MotionEvent) {
        //scale detector
        if (motionEvent.pointerCount == 2 &&
            motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER &&
            motionEvent.getToolType(1) == MotionEvent.TOOL_TYPE_FINGER
        ) {
            onScaleChange(motionEvent, _state)
        } else {
            for (i in 0 until motionEvent.pointerCount) {
                when (motionEvent.getToolType(i)) {
                    MotionEvent.TOOL_TYPE_STYLUS -> {
                        stylusHandle(motionEvent, _state)
                    }
                    MotionEvent.TOOL_TYPE_FINGER -> fingerHandle(motionEvent, i, _state)
                    else -> {}
                }
            }
        }
        updatePage()
    }

    //change file name
    fun onTitleChange(newFileName: String) {
        _state.update { it ->
            it.copy(
                name = newFileName
            )
        }
        updatePage()
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
                    it.copy(
                        lineWidthLevel = currentLineWidthLevel.roundToInt().toFloat(),
                        lineWidth = PenConst.DEFAULT_LINE_WIDTH * currentLineWidthLevel
                    )
                }
            }

            else -> {}
        }
    }

    //entry
    fun onUndo() {
        val lastBehavior: StrokeBehavior? = _state.value.undoStrokeBehaviors.popBehavior()
        when (lastBehavior?.action) {
            StrokeAction.WRITE -> undoWriteHandle(lastBehavior, _state)
            StrokeAction.ERASE -> undoEraseHandle(lastBehavior, _state)
            null -> {}
        }
    }

    //entry
    fun onRedo() {
        val lastBehavior: StrokeBehavior? = _state.value.redoStrokeBehaviors.popBehavior()
        when (lastBehavior?.action) {
            StrokeAction.WRITE -> redoWriteHandle(lastBehavior, _state)
            StrokeAction.ERASE -> redoEraseHandle(lastBehavior, _state)
            null -> {}
        }
    }

    //inserting new image from shared-storage
    fun onInsertImage(uri: Uri) {
        _state.update { it ->
            imageRepository.getImageBitmap(uri)
            it.copy(
                imageManager = it.imageManager
                    .insertImage(
                        Image(uri.toString())
                            //.loadBitmap(imageRepository)
                            .setActualPosition(it.canvasRelativePosition, it.scale)
                    ),
                isShowImagePicker = !it.isShowImagePicker
            )
        }
        updatePage()
    }

    fun showImagePicker() {
        _state.update { it ->
            it.copy(
                isShowImagePicker = !it.isShowImagePicker
            )
        }
    }

    fun onTapHandle(tapPosition: Offset) {
        _state.update { it ->
            it.copy(
                imageManager = it.imageManager.onTapHandle(
                    tapPosition,
                    it.canvasRelativePosition,
                    it.scale
                )
            )
        }
    }

    fun getImageBitmap(uri: Uri): Bitmap {
        return imageRepository.getImageBitmap(uri)
    }

    fun onDropDownMenu() {
        _state.update { it ->
            it.copy(
                isShowSettingPopupMenu = !it.isShowSettingPopupMenu
            )
        }
    }

    fun onBackgroundColorChange(code: Long) {
        _state.update { it ->
            it.copy(
                backgroundColor = code
            )
        }
        updatePage()
    }

    fun onShowBackgroundColorPicker() {
        _state.update { it ->
            it.copy(
                isShowBackgroundColorPicker = !it.isShowBackgroundColorPicker
            )
        }
    }


}