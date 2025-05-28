package com.example.goodnote.goodNote.presentation.home

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodnote.domain.Page
import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.editor.toPage
import com.example.goodnote.goodNote.presentation.editor.usecase.saveCurrentPage
import com.example.goodnote.goodNote.repository.PageRepository
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val pageRepository: PageRepository,
    private val regionRepository: RegionRepository,
    private val strokeRepository: StrokeRepository
): ViewModel() {
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
        val editorState: EditorState = EditorState(id = UUID.randomUUID().toString())
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
                var dbPages: List<Page?> = pageRepository.selectAllPages().first()
                Log.d("hehehe", "${dbPages.size}")
                if (dbPages.isEmpty()) dbPages = emptyList()
                it.copy(
                    pages = dbPages as List<Page>
                )
            }
        }
    }
}
