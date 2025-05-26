package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.zIndex
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackgroundColorPicker() {
    val viewModel: EditorViewModel = koinViewModel()
    Card(
        modifier = Modifier
            .width(450.dp)
            .padding(16.dp)
            .zIndex(2f),
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
                        onClick = { viewModel.onShowBackgroundColorPicker() }
                    ),
                tint = Color.White
            )
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .width(500.dp)
                .height(150.dp)
                .background(Color(0xFF333333)),
        ) {
            FlowRow (
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AppConst.PEN_COLORS.forEach { color ->
                    BackgroundColorBox(code = color)
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun BackgroundColorPickerPreview() {
    GoodNoteTheme {
        BackgroundColorPicker()
    }
}