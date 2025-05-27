package com.example.goodnote.goodNote.presentation.home

import com.example.goodnote.domain.Page

data class HomeState(
    val pages: List<Page> = emptyList(),
)
