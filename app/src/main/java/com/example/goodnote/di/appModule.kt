package com.example.goodnote.di

import com.example.goodnote.database.LocalDatabase
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.presentation.editor.repository.ImageRepository
import com.example.goodnote.goodNote.presentation.home.HomeViewModel
import com.example.goodnote.goodNote.repository.PageRepository
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository
import com.example.goodnote.goodNote.repository.impl.PageRepositoryImp
import com.example.goodnote.goodNote.repository.impl.RegionRepositoryImp
import com.example.goodnote.goodNote.repository.impl.StrokeRepositoryImp
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModule() = module {
    //db
    single { LocalDatabase.getDatabase(androidContext()) }
    //local db repository
    single { PageRepositoryImp(get<LocalDatabase>().pageDao()) }.bind<PageRepository>()
    single { StrokeRepositoryImp(get<LocalDatabase>().strokeDao()) }.bind<StrokeRepository>()
    single { RegionRepositoryImp(get<LocalDatabase>().regionDao()) }.bind<RegionRepository>()

    //in-program image cache
    single { ImageRepository(androidApplication()) }

    viewModel { EditorViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get()) }
}