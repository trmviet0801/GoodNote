package com.example.goodnote.di

import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { EditorViewModel() }
}