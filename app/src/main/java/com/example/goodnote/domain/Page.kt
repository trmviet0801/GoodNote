package com.example.goodnote.domain

import androidx.compose.ui.geometry.Offset
import com.example.goodnote.note.domain.Region
import com.example.goodnote.note.domain.Stroke
import com.example.goodnote.note.utils.AppConst
import java.util.UUID

data class Page(
    val id: UUID = UUID.randomUUID(),
    var name: String = AppConst.PAGE_NAME,
    var scale: Float = 1f,
    var size: List<Offset> = emptyList(),
    var currentViewOffsets: List<Offset> = emptyList(),
    var rootRegion: Region? = null,
    var latestStroke: Stroke? = null
)
