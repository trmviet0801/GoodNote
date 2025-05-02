package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.goodnote.goodNote.action.ScrollAction
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PenWidthPicker() {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.minus),
            contentDescription = R.string.minus.toString(),
            modifier = Modifier
                .size(AppConst.ICON_SIZE)
                .clickable(
                    enabled = true,
                    onClick = {
                        editorViewModel.onPenWidthChange(ScrollAction.RIGHT, 1f)
                    }
                ),
            tint = Color.White
        )
        PenWidthSlice()
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.add),
            contentDescription = R.string.add.toString(),
            modifier = Modifier
                .size(AppConst.ICON_SIZE)
                .clickable(
                    enabled = true,
                    onClick = {
                        editorViewModel.onPenWidthChange(ScrollAction.LEFT, 1f)
                    }
                ),
            tint = Color.White
        )
    }
}

@PreviewLightDark
@Composable
private fun PenWidthPickerPreview() {
    GoodNoteTheme {
        PenWidthPicker()
    }
}