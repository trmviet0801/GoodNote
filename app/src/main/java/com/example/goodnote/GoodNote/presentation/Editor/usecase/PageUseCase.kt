package com.example.goodnote.goodNote.presentation.editor.usecase

import android.util.Log
import com.example.goodnote.domain.Page
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.editor.toPage
import com.example.goodnote.goodNote.repository.PageRepository
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository

suspend fun saveCurrentPage(
    editorState: EditorState,
    pageRepository: PageRepository,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    pageRepository.insertPage(editorState.toPage())
    editorState.rootRegion?.let { rootRegion ->
        saveRootRegion(rootRegion, regionRepository, strokeRepository)
    }
    saveDownestAndRightest(editorState, strokeRepository)
}

suspend fun updateCurrentPage(
    editorState: EditorState,
    pageRepository: PageRepository,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    pageRepository.insertPage(editorState.toPage())
    editorState.rootRegion?.let { region ->
        updateRootRegion(region, regionRepository, strokeRepository)
    }
    saveDownestAndRightest(editorState, strokeRepository)
}

suspend fun saveDownestAndRightest(
    editorState: EditorState,
    strokeRepository: StrokeRepository
) {
    editorState.rightest?.let { saveStroke(it, strokeRepository) }
    editorState.downest?.let { saveStroke(it, strokeRepository) }
}