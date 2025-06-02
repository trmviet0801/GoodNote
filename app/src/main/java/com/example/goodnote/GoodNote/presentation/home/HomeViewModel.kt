package com.example.goodnote.goodNote.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.domain.Page
import com.example.goodnote.domain.Stroke
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.editor.toPage
import com.example.goodnote.goodNote.presentation.editor.usecase.saveCurrentPage
import com.example.goodnote.goodNote.repository.PageRepository
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val pageRepository: PageRepository,
    private val regionRepository: RegionRepository,
    private val strokeRepository: StrokeRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HomeState()
    )

    init {
        getAllPages()
    }

    fun createPage() {
        val editorState: EditorState =
            EditorState(
                id = UUID.randomUUID().toString(),
                timeStamps = System.currentTimeMillis(),
                latestTimeStamp = System.currentTimeMillis()
            )
        val page: Page = editorState.toPage()
        viewModelScope.launch {
            saveCurrentPage(
                editorState,
                pageRepository,
                regionRepository,
                strokeRepository
            )
        }
        _state.update {
            it.copy(
                pages = it.pages + page
            )
        }
    }

    fun getAllPages() {
        viewModelScope.launch {
            _state.update { it ->
                var dbPages: List<Page?> =
                    if (it.isCanvasSelected) pageRepository.selectAllPagesOrderByName()
                        .first() else pageRepository.selectAllPagesOrderByLatestTimeStamp().first()
                if (dbPages.isEmpty()) dbPages = emptyList()
                it.copy(
                    pages = dbPages as List<Page>
                )
            }
        }
    }

    fun onToggleButtonActive() {
        viewModelScope.launch {
            _state.update { state ->
                val currentPages: List<Page?> = if (!state.isCanvasSelected) pageRepository.selectAllPagesOrderByName().first() else state.pages
                state.copy(
                    isCanvasSelected = true,
                    pages = currentPages as List<Page>
                )
            }
        }
    }

    fun onToggleButtonInactive() {
        viewModelScope.launch {
            _state.update { state ->
                val currentPages: List<Page?> = if (state.isCanvasSelected) pageRepository.selectAllPagesOrderByLatestTimeStamp().first() else state.pages
                state.copy(
                    isCanvasSelected = false,
                    pages =  currentPages as List<Page>
                )
            }
        }
    }

    suspend fun getMiniStrokesOfPage(rootRegionId: String?): List<Stroke> {
        if (rootRegionId == null) return emptyList()

        val regionEntity = regionRepository.selectRegionEntityWithId(rootRegionId).firstOrNull()
            ?: return emptyList()

        val strokes = mutableListOf<Stroke>()

        regionEntity.primaryStrokeId?.let { id ->
            strokeRepository.selectStrokeWithId(id).firstOrNull()?.let { strokes.add(it) }
        }

        regionEntity.overlapsStrokesId
            .filterNotNull()
            .forEach { id ->
                strokeRepository.selectStrokeWithId(id).firstOrNull()?.let { strokes.add(it) }
            }

        return strokes
    }

}
