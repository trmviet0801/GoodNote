package com.example.goodnote.goodNote.presentation.editor.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ImagePicker() {
    val viewModel: EditorViewModel = koinViewModel<EditorViewModel>()
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val isLaunchImagePicker = state.value.isShowImagePicker

    val pickerImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null)
            viewModel.onInsertImage(uri)
    }

    LaunchedEffect(
        isLaunchImagePicker
    ) {
        if (isLaunchImagePicker == true) {
            pickerImageLauncher.launch("image/*")
        }
    }
}

@Composable
@PreviewLightDark
private fun ImagePickerPreview() {
    GoodNoteTheme {
        ImagePicker()
    }
}