package com.example.goodnote.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import coil3.size.Scale
import com.example.goodnote.goodNote.domain.Image
import com.example.goodnote.goodNote.domain.ImageManager
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.domain.Stroke
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: List<Offset>,
    val rootRegion: Region?,
    val oversizeStrokes: List<Stroke> = emptyList(),
    val removedStrokes: List<Stroke> = emptyList(),
    var imageManager: ImageManager = ImageManager(emptyList()),
    val backgroundColor: Long = 0xFF000000,
    var canvasRelativePosition: Offset,
    var scale: Float
)

fun Page.toState(): EditorState {
    val editorState: EditorState = EditorState(id = this.id)
    editorState.name = this.name
    editorState.size = this.size
    editorState.rootRegion = this.rootRegion
    editorState.oversizeStrokes = this.oversizeStrokes
    editorState.removedStrokes = this.removedStrokes
    editorState.imageManager = this.imageManager
    editorState.backgroundColor = this.backgroundColor
    editorState.canvasRelativePosition = this.canvasRelativePosition
    editorState.scale = this.scale
    return editorState
}