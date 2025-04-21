package com.example.goodnote.di

import com.example.goodnote.note.presentation.Editor.EditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModel { EditorViewModel() }
}