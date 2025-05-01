package com.example.goodnote.goodNote.presentation.Editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.domain.Stroke
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.goodNote.utils.PenConst

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

    //scale
    var scaleFactor: Float = 0f,

    //pen
    var color: Long = PenConst.DEFAULT_PEN_COLOR_1,
    var lineWidth: Float = PenConst.DEFAULT_LINE_WIDTH,

    //bars
    var isFullScreen: Boolean = true,
)
