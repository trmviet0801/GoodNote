package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.ScaleGestureDetector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodnote.note.utils.AppConvertor
import org.koin.androidx.compose.koinViewModel
import org.koin.viewmodel.getViewModelKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorScreen(innerPadding: PaddingValues) {
    val editorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()

    val context = LocalContext.current
    val scaleGestureDetector = ScaleGestureDetector(context, object :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Log.d("scalee", "hererer")
            return true
        }
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
            .onGloballyPositioned { layoutCoordinates ->
                editorViewModel.adjustPageSize(layoutCoordinates)
            }
    ) {
        Text(
            text = if (state.value.page.rootRegion == null) "null" else "${AppConvertor.pxToDp(state.value.page.rootRegion?.boundary?.actualWidth ?: 0f)}",
            modifier = Modifier.padding(top = 50.dp)
        )
        Canvas(
            modifier = Modifier
                .pointerInteropFilter { motionEvent ->
                    scaleGestureDetector.onTouchEvent(motionEvent)
                    true
                }
                .width(AppConvertor.pxToDp(state.value.page.rootRegion?.boundary?.actualWidth ?: 100f))
                .height(AppConvertor.pxToDp(state.value.page.rootRegion?.boundary?.actualHeight ?: 100f))
                .background(Color.Blue)
        ) { }
    }
}