package com.example.goodnote.note.presentation.Editor

import android.util.Log
import android.view.ScaleGestureDetector
import android.view.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodnote.note.domain.toPath
import com.example.goodnote.note.presentation.Editor.components.PenSelection
import com.example.goodnote.note.presentation.Editor.components.TopBar
import com.example.goodnote.note.utils.AppConvertor
import com.example.goodnote.note.utils.PenConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.viewmodel.getViewModelKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorScreen(innerPadding: PaddingValues) {
    val editorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                editorViewModel.scaling(detector)
                return true
            }
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF72A0C1))
            .onGloballyPositioned { layoutCoordinates ->
                editorViewModel.adjustPageSize(layoutCoordinates)
            }
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        TopBar(focusRequester)
        PenSelection()
        Canvas(
            modifier = Modifier
                .pointerInteropFilter { motionEvent ->
                    focusManager.clearFocus()
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
                .background(Color.Black)) {
            drawPath(
                path = state.value.latestStroke.toPath(state.value.canvasRelativePosition),
                color = Color(state.value.latestStroke.color),
                style = Stroke(
                    state.value.latestStroke.lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawPath(
                path = (state.value.rootRegion?.primaryStroke?.toPath(state.value.canvasRelativePosition)
                    ?: Path()),
                color = Color(state.value.rootRegion?.primaryStroke?.color ?: 0xffffff),
                style = Stroke(
                    state.value.rootRegion?.primaryStroke?.lineWidth ?: PenConst.DEFAULT_LINE_WIDTH,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            state.value.rootRegion?.overlapsStrokes?.forEach { stroke ->
                drawPath(
                    path = stroke.toPath(state.value.canvasRelativePosition),
                    color = Color(stroke.color),
                    style = Stroke(stroke.lineWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

        }
    }
}

@PreviewLightDark
@Composable
private fun EditorScreenPreview() {
    GoodNoteTheme {
        EditorScreen(PaddingValues(12.dp))
    }
}