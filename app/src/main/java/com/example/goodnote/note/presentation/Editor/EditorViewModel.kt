package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.note.domain.Boundary
import com.example.goodnote.note.domain.Dot
import com.example.goodnote.note.domain.Region
import com.example.goodnote.note.domain.Stroke
import com.example.goodnote.note.domain.calActualSize
import com.example.goodnote.note.domain.updateScaledPositions
import com.example.goodnote.note.utils.AppConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.collections.toList

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
                    rootRegion = region
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
                else -> {}
            }
        }
    }

    private fun stylusHandle(motionEvent: MotionEvent) {
        val action = motionEvent.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                _state.update { it ->
                    val currentStroke = it.latestStroke
                    var currentDots = currentStroke.dots.toMutableList()
                    currentDots = mutableListOf<Dot>()

                    it.copy(
                        latestStroke = Stroke(dots = currentDots.toList())
                    )
                }
            }

            MotionEvent.ACTION_MOVE -> {
                _state.update { it ->
                    val currentStroke = it.latestStroke
                    val currentDots = currentStroke.dots.toMutableList()
                    currentDots.add(
                        Dot(
                            motionEvent.x / it.scale,
                            motionEvent.y / it.scale,
                            motionEvent.x,
                            motionEvent.y
                        )
                    )
                    Log.d("scaless", motionEvent.x.toString())

                    it.copy(
                        latestStroke = Stroke(dots = currentDots.toList())
                    )
                }
            }

            MotionEvent.ACTION_UP -> {
                _state.update { it ->
                    var currentLatestStroke = it.latestStroke
                    val currentRootRegion = it.rootRegion
                    var currentPage = it

                    currentRootRegion!!.insert(currentLatestStroke)
                    currentLatestStroke = Stroke()

                    it.copy(
                        latestStroke = currentLatestStroke,
                        rootRegion = currentRootRegion
                    )
                }
            }
        }
    }

    private fun adjustScale(isIncrease: Boolean) {
        _state.update { it ->
            var currentScale = it.scale
            if (isIncrease) currentScale += AppConst.SCALE_LEVEL else currentScale -= AppConst.SCALE_LEVEL
            val newRootRegion = it.rootRegion!!.scaleStrokes(currentScale)
            it.copy(
                scale = currentScale,
                rootRegion = newRootRegion
            )
        }
    }

    fun scaling(detector: ScaleGestureDetector) {
        val scaleFactor = detector.scaleFactor
        if (scaleFactor == 1f) return
        //zoom in
        if (scaleFactor > 1f) adjustScale(true) else adjustScale(false)
    }
}