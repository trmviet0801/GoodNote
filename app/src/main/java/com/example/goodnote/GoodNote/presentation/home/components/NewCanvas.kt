package com.example.goodnote.goodNote.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.home.HomeViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewCanvas() {
    val viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsState()
    val isShowSearchBox = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(Color(AppConst.HOME_BACKGROUND_PRIMARY_COLOR),
                shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
            .clickable(
                enabled = true,
                onClick = { viewModel.createPage() }
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = stringResource(R.string.add),
            modifier = Modifier
                .size(18.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun NewCanvasPreview() {
    GoodNoteTheme {
        NewCanvas()
    }
}