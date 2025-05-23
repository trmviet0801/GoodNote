package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PenSelection() {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppConst.BARs_HEIGHT)
            .height(500.dp)
            .background(Color(0xFF2C3539)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(56.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.erase),
                contentDescription = R.string.erase.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE)
                    .clickable(
                        enabled = true,
                        onClick = {editorViewModel.onEraser()}
                    ),
                tint = if (!state.value.isEraser) Color.White else Color.Red
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.pen),
                contentDescription = R.string.pen.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE)
                    .clickable(
                        enabled = true,
                        onClick = {editorViewModel.onDisplayPenWidthSelection()}
                    ),
                tint = Color(state.value.color)
            )
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorSelection(state.value.savedColors[0], 0)
                ColorSelection(state.value.savedColors[1], 1)
                ColorSelection(state.value.savedColors[2], 2)
            }
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back),
                    contentDescription = R.string.back.toString(),
                    modifier = Modifier
                        .size(AppConst.ICON_SIZE)
                        .clickable(
                            enabled = true,
                            onClick = {editorViewModel.onUndo()}
                        ),
                    tint = Color.White
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back),
                    contentDescription = R.string.back.toString(),
                    modifier = Modifier
                        .size(AppConst.ICON_SIZE)
                        .rotate(180f)
                        .clickable(
                            enabled = true,
                            onClick = {editorViewModel.onRedo()}
                        ),
                    tint = Color.White
                )
            }
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.rec),
                contentDescription = R.string.rec.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE),
                tint = Color.White
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PenSelectionPreview() {
    GoodNoteTheme {
        PenSelection()
    }
}