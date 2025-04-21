package com.example.goodnote.note.presentation.Editor

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.domain.Page
import com.example.goodnote.note.domain.Boundary
import com.example.goodnote.note.domain.Region
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
}