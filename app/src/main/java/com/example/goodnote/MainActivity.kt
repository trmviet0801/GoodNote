package com.example.goodnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.goodnote.di.appModule
import com.example.goodnote.goodNote.presentation.editor.EditorScreen
import com.example.goodnote.goodNote.presentation.editor.components.AddResourceMenu
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodNoteTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    EditorScreen()
                }
            }
        }
    }
}