package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PenPicker() {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()
    Card (
        modifier = Modifier
            .width(450.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color(0xFF333333)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (state.value.savedColors.contains(state.value.color)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.star),
                    contentDescription = R.string.star.toString(),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(AppConst.ICON_SIZE),
                    tint = Color.Yellow
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.star),
                    contentDescription = R.string.star.toString(),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(AppConst.ICON_SIZE)
                        .clickable(
                            enabled = true,
                            onClick = {
                                editorViewModel.onSavedColorChange(
                                    state.value.color,
                                    state.value.currentSavedColorIndex
                                )
                            }
                        ),
                    tint = Color.White
                )
            }
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.close),
                contentDescription = R.string.close.toString(),
                modifier = Modifier
                    .padding(8.dp)
                    .size(AppConst.ICON_SIZE)
                    .clickable(
                        enabled = true,
                        onClick = {editorViewModel.onDisplayPenWidthSelection()}
                    ),
                tint = Color.White
            )
        }
        PenWidthPicker()
        SmallColorPicker()
    }
}

@PreviewLightDark
@Composable
private fun PenPickerPreview() {
    GoodNoteTheme {
        PenPicker()
    }
}