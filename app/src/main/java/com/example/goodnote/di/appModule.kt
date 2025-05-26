package com.example.goodnote.di

import android.app.Application
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    viewModel { EditorViewModel(androidApplication()) }
}