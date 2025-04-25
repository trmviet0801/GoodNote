package com.example.goodnote.note.presentation.Editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.note.domain.Region
import com.example.goodnote.note.domain.Stroke
import com.example.goodnote.note.utils.AppConst
import com.example.goodnote.note.utils.PenConst

@Immutable
data class EditorState(
    //file + canvas
    var name: String = AppConst.PAGE_NAME,
    var scale: Float = 1f,
    var size: List<Offset> = emptyList(),
    var scrollOffset: Offset = Offset.Zero,
    var oversizeStrokes: List<Stroke> = emptyList<Stroke>(),
    var rootRegion: Region? = null,
    var latestStroke: Stroke = Stroke(),
    var translate: Offset = Offset.Zero,
    var screenWidth: Int = 0,
    var screenHeight: Int = 0,
    var canvasRelativePosition: Offset = Offset.Zero,
    var rightest: Stroke? = null,
    var downest: Stroke? = null,
    var removedStrokes: List<Stroke> = emptyList(),

    //pen
    var color: Long = PenConst.DEFAULT_PEN_COLOR_1,
    var lineWidth: Float = PenConst.DEFAULT_LINE_WIDTH
)
