package com.example.goodnote.domain

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.goodnote.goodNote.domain.ImageManager
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.presentation.editor.EditorState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

//EditorStateEntity
@Entity(tableName = "pages")
data class Page(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: List<Offset>,
    val rootRegionId: String?,
    val oversizeStrokeIds: List<String> = emptyList(),
    val removedStrokeIds: List<String> = emptyList(),
    var imageManager: ImageManager = ImageManager(emptyList()),
    var backgroundColor: Long = 0xFF000000,
    var canvasRelativePosition: Offset,
    var scale: Float,
    var screenWidth: Int = 0,
    var screenHeight: Int = 0,
    var rightestId: String? = null,
    var downestId: String? = null,
    val timeStamps: Long,
    var latestTimeStamp: Long
)

fun Page.toState(
    rootRegion: Region? = null,
    oversizeStrokes: List<Stroke> = emptyList(),
    removedStrokes: List<Stroke> = emptyList(),
    rightest: Stroke?,
    downest: Stroke?
): EditorState {
    val editorState: EditorState = EditorState(
        id = this.id,
        timeStamps = this.timeStamps,
        latestTimeStamp = this.latestTimeStamp
    )
    editorState.name = this.name
    editorState.size = this.size
    editorState.rootRegion = rootRegion
    editorState.oversizeStrokes = oversizeStrokes
    editorState.removedStrokes = removedStrokes
    editorState.imageManager = this.imageManager
    editorState.backgroundColor = this.backgroundColor
    editorState.canvasRelativePosition = this.canvasRelativePosition
    editorState.scale = this.scale
    editorState.screenWidth = this.screenWidth
    editorState.screenHeight = this.screenHeight
    editorState.rightest = rightest
    editorState.downest = downest
    return editorState
}

@RequiresApi(Build.VERSION_CODES.O)
fun Page.timeStampToDateTime(): String {
    val instant = Instant.ofEpochMilli(this.timeStamps)
    val zdt = instant.atZone(ZoneId.systemDefault())

    val day = zdt.dayOfMonth
    val year = zdt.year
    val hourMinute = zdt.format(DateTimeFormatter.ofPattern("HH:mm"))

    // Map month number to "ThX"
    val monthTh = "Th${zdt.monthValue}"

    return "$day $monthTh, $year $hourMinute"
}