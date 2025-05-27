package com.example.goodnote.goodNote.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.home.HomeViewModel
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddPage() {
    val viewmodel: HomeViewModel = koinViewModel<HomeViewModel>()
    Box(
        modifier = Modifier
            .width(24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    enabled = true,
                    onClick = {
                        viewmodel.createPage()
                    }
                )
        )
    }
}

@Composable
@PreviewLightDark
private fun AddPagePreview() {
    GoodNoteTheme {
        AddPage()
    }
}