package com.example.goodnote.note.presentation.Editor

import androidx.compose.runtime.Immutable
import com.example.goodnote.domain.Page
import com.example.goodnote.note.domain.Region

@Immutable
data class EditorState(
    val page: Page = Page()
)
