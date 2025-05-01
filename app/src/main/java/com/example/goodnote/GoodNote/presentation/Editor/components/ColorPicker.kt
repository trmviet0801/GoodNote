package com.example.goodnote.goodNote.presentation.Editor.components

import android.text.Layout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.Editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(index: Int) {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    Card(
        modifier = Modifier
            .width(450.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color(0xFF333333)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.close),
                contentDescription = R.string.close.toString(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(AppConst.ICON_SIZE)
                    .clickable(
                        enabled = true,
                        onClick = {editorViewModel.onDisplayColorPicker()}
                    ),
                tint = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            FlowRow (
                modifier = Modifier
                    .width(280.dp)
                    .padding(16.dp)
            ) {
                AppConst.PEN_COLORS.forEach { color ->
                    ColorBox(color, index)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ColorPickerPreview() {
    GoodNoteTheme {
        ColorPicker(0)
    }
}