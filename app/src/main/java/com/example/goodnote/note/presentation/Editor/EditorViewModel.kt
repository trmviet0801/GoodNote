package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.domain.Page
import com.example.goodnote.note.domain.Boundary
import com.example.goodnote.note.domain.Dot
import com.example.goodnote.note.domain.Region
import com.example.goodnote.note.domain.Stroke
import com.example.goodnote.note.domain.calActualSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class EditorViewModel(): ViewModel() {
    private val _state = MutableStateFlow(EditorState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            EditorState()
        )

    fun adjustPageSize(layoutCoordinates: LayoutCoordinates) {
        if (_state.value.page.rootRegion == null) {
            val boundary = boundaryFromPositionAndSize(
                layoutCoordinates.positionOnScreen(),
                layoutCoordinates.size.width,
                layoutCoordinates.size.height
            )
            val region: Region = Region(boundary = boundary)
            region.isRoot = true
            _state.update { it ->
                val currentPage = it.page.copy(rootRegion = region)
                it.copy(
                    page = currentPage
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
                    val currentStroke = it.page.latestStroke
                    var currentDots = currentStroke.dots.toMutableList()
                    currentDots = mutableListOf<Dot>()

                    val currentPage = it.page.copy(latestStroke = Stroke(dots = currentDots.toList()))
                    it.copy(
                        page = currentPage
                    )
                }
                Log.d("scalee", "down")
            }
            MotionEvent.ACTION_MOVE -> {
                _state.update { it ->
                    val currentStroke = it.page.latestStroke
                    val currentDots = currentStroke.dots.toMutableList()
                    currentDots.add(Dot(motionEvent.x, motionEvent.y))

                    val currentPage = it.page.copy(latestStroke = Stroke(dots = currentDots.toList()))
                    it.copy(
                        page = currentPage
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                _state.update { it ->
                    var currentLatestStroke = it.page.latestStroke
                    val currentRootRegion = it.page.rootRegion
                    var currentPage = it.page

                    currentRootRegion!!.insert(currentLatestStroke)
                    Log.d("scalee 1", currentLatestStroke.toString())
                    currentLatestStroke = Stroke()
                    currentPage = currentPage.copy(
                        latestStroke = currentLatestStroke,
                        rootRegion = currentRootRegion
                    )

                    it.copy(
                        page = currentPage
                    )
                }
            }
        }
    }
}