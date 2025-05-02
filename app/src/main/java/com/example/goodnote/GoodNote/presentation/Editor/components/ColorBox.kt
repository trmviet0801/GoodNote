package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ColorBox(code: Long, index: Int = -1) {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()
    if (code == state.value.color) {
        Box (
            modifier = Modifier
                .width(AppConst.ICON_SIZE)
                .height(AppConst.ICON_SIZE)
                .background(Color(code))
                .border(1.dp, Color.Black)
                .clickable(
                    enabled = true,
                    onClick = {
                        editorViewModel.onPenColorChange(code)
                    }
                )
        ) {

        }
    } else {
        Box (
            modifier = Modifier
                .width(AppConst.ICON_SIZE)
                .height(AppConst.ICON_SIZE)
                .background(Color(code))
                .clickable(
                    enabled = true,
                    onClick = {
                        editorViewModel.onPenColorChange(code)
                    }
                )
        ) {

        }
    }
}

@PreviewLightDark
@Composable
private fun ColorBoxPreview() {
    GoodNoteTheme { 
        ColorBox(AppConst.PEN_COLORS[0])
    }
}