package com.example.goodnote.di

import android.app.Application
import com.example.goodnote.database.PageDatabase
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.presentation.editor.repository.ImageRepository
import com.example.goodnote.goodNote.presentation.home.HomeViewModel
import com.example.goodnote.goodNote.repository.PageRepository
import com.example.goodnote.goodNote.repository.PageRepositoryImp
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModule() = module {
    single { PageRepositoryImp(PageDatabase.getDatabase(androidContext()).pageDao()) }.bind<PageRepository>()
    single { ImageRepository(androidApplication()) }
    viewModel { EditorViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
}