package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PenWidthSlice() {
    var editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    var state = editorViewModel.state.collectAsState()
    Canvas(
        modifier = Modifier
            .width(250.dp)
            .height(50.dp)
            .padding(8.dp)
            .pointerInteropFilter{motionEvent ->
                editorViewModel.handlePenSelectionTouch(motionEvent)
                true
            },
    ) {
        val spaceBetweenDots = size.width / 7
        val centerY = size.height / 2
        for (i in AppConst.PEN_WIDTH_LEVEL_MIN..AppConst.PEN_WIDTH_LEVEL_MAX) {
            drawCircle(
                Color(0xFFC0C0C0),
                center = Offset(i * spaceBetweenDots, centerY),
                radius = 10f
            )
        }
        drawCircle(
            Color(0xFF696969),
            center = Offset(state.value.lineWidthLevel.roundToInt() * spaceBetweenDots, centerY),
            radius = 30f
        )
    }
}

@PreviewLightDark
@Composable
private fun PenWidthSlicePreview() {
    GoodNoteTheme {
        PenWidthSlice()
    }
}