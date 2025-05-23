package com.example.goodnote.goodNote.presentation.editor.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddResourceMenu() {
    val editorViewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()
    Card (
        modifier = Modifier
            .wrapContentWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        //shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color(0xFF333333)
        )
    ) {
        Text(
            text = stringResource(R.string.add_image),
            modifier = Modifier.padding(16.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        editorViewModel.showImagePicker()
                    }
                ),
            color = Color.White
        )
        if (state.value.isShowImagePicker)
            ImagePicker()
    }
}

@PreviewLightDark
@Composable
fun AddResourceMenuPreview() {
    GoodNoteTheme {
        AddResourceMenu()
    }
}