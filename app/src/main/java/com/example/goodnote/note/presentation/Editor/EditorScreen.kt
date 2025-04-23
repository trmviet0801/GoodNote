package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.ScaleGestureDetector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodnote.note.domain.toPath
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
            editorViewModel.scaling(detector)
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
            text = if (state.value.rootRegion == null) "null" else "${AppConvertor.pxToDp(state.value.rootRegion?.boundary?.actualWidth ?: 0f)}",
            modifier = Modifier.padding(top = 50.dp)
        )
        Canvas(
            modifier = Modifier
                .pointerInteropFilter { motionEvent ->
                    scaleGestureDetector.onTouchEvent(motionEvent)
                    editorViewModel.handleInput(motionEvent)
                    true
                }
                .width(
                    AppConvertor.pxToDp(
                        state.value.rootRegion?.boundary?.actualWidth ?: 100f
                    ) * state.value.scale
                )
                .height(
                    AppConvertor.pxToDp(
                        state.value.rootRegion?.boundary?.actualHeight ?: 100f
                    ) * state.value.scale
                )
                .background(Color.Black)
        ) {
                drawPath(
                    path = state.value.latestStroke.toPath(state.value.canvasRelativePosition),
                    color = Color.White,
                    style = Stroke(5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                drawPath(
                    path = (state.value.rootRegion?.primaryStroke?.toPath(state.value.canvasRelativePosition) ?: Path()),
                    color = Color.White,
                    style = Stroke(5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                state.value.rootRegion?.overlapsStrokes?.forEach { stroke ->
                    drawPath(
                        path = stroke.toPath(state.value.canvasRelativePosition),
                        color = Color.White,
                        style = Stroke(5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
        }
    }
}