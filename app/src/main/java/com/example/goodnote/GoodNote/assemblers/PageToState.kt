package com.example.goodnote.goodNote.assemblers

import com.example.goodnote.domain.Page
import com.example.goodnote.domain.Stroke
import com.example.goodnote.domain.toState
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository
import kotlinx.coroutines.flow.first

suspend fun pageToState(
    page: Page,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
): EditorState {
    val rootRegion: Region? = page.rootRegionId?.let { id ->
         regionRepository
             .selectRegionEntityWithId(id)
             .first()?.let { regionEntity ->
                 assembleRegion(regionEntity, strokeRepository, regionRepository)
             }
    }
    val oversizeStrokes: List<Stroke> = loadStrokesByIds(page.oversizeStrokeIds, strokeRepository)
    val removedStrokes: List<Stroke> = loadStrokesByIds(page.removedStrokeIds, strokeRepository)

    val rightest: Stroke? = page.rightestId?.let { loadStrokeById(it, strokeRepository) }
    val downest: Stroke? = page.downestId?.let { loadStrokeById(it, strokeRepository) }

    return page.toState(
        rootRegion,
        oversizeStrokes,
        removedStrokes,
        rightest,
        downest
    )
}

suspend fun loadStrokeById(
    id: String,
    strokeRepository: StrokeRepository
): Stroke? {
    return strokeRepository.selectStrokeWithId(id).first()
}

suspend fun loadStrokesByIds(
    ids: List<String>,
    strokeRepository: StrokeRepository
): List<Stroke> {
    return ids.mapNotNull { id ->
        strokeRepository.selectStrokeWithId(id).first()
    }
}