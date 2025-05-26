package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.ui.theme.GoodNoteTheme

@Composable
fun ImageDot() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(10.dp)
            .height(10.dp)
            .background(Color.White),
    ) {

    }
}

@Composable
@PreviewLightDark
private fun ImageDotPreview() {
    GoodNoteTheme {
        ImageDot()
    }
}