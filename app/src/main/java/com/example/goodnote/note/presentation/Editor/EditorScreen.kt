package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.ScaleGestureDetector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.koin.viewmodel.getViewModelKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorScreen(innerPadding: PaddingValues) {
    val editorViewModel = koinViewModel<EditorViewModel>()

    val context = LocalContext.current
    val scaleGestureDetector = ScaleGestureDetector(context, object :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return true
        }
    })

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->

                }
                .pointerInteropFilter { motionEvent ->
                    scaleGestureDetector.onTouchEvent(motionEvent)
                    true
                }
        ) { }
    }
}