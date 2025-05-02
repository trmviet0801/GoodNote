package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ColorSelection(bgColor: Long, index: Int) {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    Canvas(
        modifier = Modifier
            .width(AppConst.ICON_SIZE)
            .height(AppConst.ICON_SIZE)
            .clickable(
                enabled = true,
                onClick = {
                    editorViewModel.onPenColorChange(bgColor)
                }
            )
    ) {
        drawCircle(
            color = Color(bgColor),
        )
    }
}

@PreviewLightDark
@Composable
private fun ColorSelectionPreview() {
    GoodNoteTheme {
        //ColorSelection(Color.Black)
    }
}