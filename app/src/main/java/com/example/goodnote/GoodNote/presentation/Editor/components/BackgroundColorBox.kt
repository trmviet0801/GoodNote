package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun BackgroundColorBox(code: Long) {
    val viewmodel: EditorViewModel = koinViewModel<EditorViewModel>()
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(24.dp)
            .background(_root_ide_package_.androidx.compose.ui.graphics.Color(code))
            .clickable(
                enabled = true,
                onClick = { viewmodel.onBackgroundColorChange(code) }
            )
    ) {}
}

@PreviewLightDark
@Composable
private fun BackgroundColorBoxPreview() {
    GoodNoteTheme {
        BackgroundColorBox(0xffff)
    }
}