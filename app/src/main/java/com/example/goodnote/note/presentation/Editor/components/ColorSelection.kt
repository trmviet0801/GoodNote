package com.example.goodnote.note.presentation.Editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.goodnote.note.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme

@Composable
fun ColorSelection(bgColor: Color) {
    Canvas(
        modifier = Modifier
            .width(AppConst.ICON_SIZE)
            .height(AppConst.ICON_SIZE)
    ) {
        drawCircle(
            color = bgColor,
        )
    }
}

@PreviewLightDark
@Composable
private fun ColorSelectionPreview() {
    GoodNoteTheme {
        ColorSelection(Color.Black)
    }
}