package com.example.goodnote

import com.example.goodnote.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GoodNoteApp: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GoodNoteApp)
            modules(appModule())
        }
    }
}