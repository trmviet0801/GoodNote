package com.example.goodnote.goodNote.presentation.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.goodnote.domain.Page
import com.example.goodnote.goodNote.domain.Image
import com.example.goodnote.goodNote.domain.ImageManager
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.domain.Stroke
import com.example.goodnote.goodNote.presentation.model.StrokeBehaviors
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.goodNote.utils.PenConst
import java.util.UUID

@Immutable
data class EditorState(
    val id: String = UUID.randomUUID().toString(),
    //file + canvas
    var name: String = AppConst.PAGE_NAME,
    var scale: Float = 1f,
    var size: List<Offset> = emptyList(),
    var oversizeStrokes: List<Stroke> = emptyList<Stroke>(),
    var rootRegion: Region? = null,
    var latestStroke: Stroke = Stroke(),
    var translate: Offset = Offset.Zero,
    var screenWidth: Int = 0,
    var screenHeight: Int = 0,
    //screen position - virtual camera offset
    var canvasRelativePosition: Offset = Offset.Zero,
    var rightest: Stroke? = null,
    var downest: Stroke? = null,
    var removedStrokes: List<Stroke> = emptyList(),

    //scroll
    var scrollOffset: Offset = Offset.Zero,
    var penWidthScrollOffset: Offset = Offset.Zero,

    //scale
    //scale factor between two fingers when scaling
    var scaleFactor: Float = 0f,

    //pen
    var color: Long = PenConst.DEFAULT_PEN_COLOR_1,
    var savedColors: List<Long> = listOf<Long>(
        PenConst.DEFAULT_PEN_COLOR_1,
        PenConst.DEFAULT_PEN_COLOR_2,
        PenConst.DEFAULT_PEN_COLOR_3
    ),
    var lineWidthLevel: Float = 3f,
    var lineWidth: Float = PenConst.DEFAULT_LINE_WIDTH * lineWidthLevel,
    var widthSlicerXLevel: Int = 0,
    var currentSavedColorIndex: Int = 0,
    var isEraser: Boolean = false,

    //bars
    var isFullScreen: Boolean = true,
    var isShowPenPicker: Boolean = false,
    var isShowColorPicker: Boolean = true,

    //forward + backward
    val undoStrokeBehaviors: StrokeBehaviors = StrokeBehaviors(),
    val redoStrokeBehaviors: StrokeBehaviors = StrokeBehaviors(),

    //inserting image
    val isShowImagePicker: Boolean = false,
    var imageManager: ImageManager = ImageManager(emptyList()),
    var imageScrollOffset: Offset = Offset.Zero,

    //moving image
    var lastMovingImage: Image? = null,

    //setting
    var isShowSettingPopupMenu: Boolean = false,

    //background
    var backgroundColor: Long = 0xFF000000,
    var isShowBackgroundColorPicker: Boolean = false
)

fun EditorState.toPage(): Page {
    return Page(
        id = this.id,
        name = this.name,
        size = this.size,
        rootRegion = this.rootRegion,
        oversizeStrokes = this.oversizeStrokes,
        removedStrokes = this.removedStrokes,
        imageManager = this.imageManager,
        backgroundColor = this.backgroundColor
    )
}