package com.example.goodnote.note.presentation.Editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.domain.Page
import com.example.goodnote.note.domain.Region
import com.example.goodnote.note.domain.Stroke
import com.example.goodnote.note.utils.AppConst

@Immutable
data class EditorState(
    var name: String = AppConst.PAGE_NAME,
    var scale: Float = 1f,
    var size: List<Offset> = emptyList(),
    var scrollOffset: Offset = Offset.Zero,
    var rootRegion: Region? = null,
    var latestStroke: Stroke = Stroke(),
    var translate: Offset = Offset.Zero,
    var screenWidth: Int = 0,
    var screenHeight: Int = 0,
    var canvasRelativePosition: Offset = Offset.Zero
)
