package com.example.goodnote.goodNote.presentation.Editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmallColorPicker() {
    FlowRow (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AppConst.PEN_COLORS.forEachIndexed { index, color ->
            if (index <= 12)
                ColorBox(code = color)
        }
    }
}

@PreviewLightDark
@Composable
private fun SmallColorPickerPreview() {
    GoodNoteTheme {
        SmallColorPicker()
    }
}