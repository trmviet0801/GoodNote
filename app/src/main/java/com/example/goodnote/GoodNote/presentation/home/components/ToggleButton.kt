package com.example.goodnote.goodNote.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun ToggleButton() {
    val viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(AppConst.HOME_BACKGROUND_PRIMARY_COLOR)),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Card (
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        viewModel.onToggleButtonActive()
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(
                    if (state.value.isCanvasSelected) AppConst.HOME_BACKGROUND_SECONDARY_COLOR else AppConst.HOME_BACKGROUND_PRIMARY_COLOR
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            if (state.value.isCanvasSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.canvases),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.canvases),
                            color = Color(AppConst.TEXT_PRIMARY_COLOR)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.canvases),
                            contentDescription = null,
                            tint = Color(AppConst.TEXT_DISABLE_COLOR)
                        )
                        Text(
                            text = stringResource(R.string.canvases),
                            color = Color(AppConst.TEXT_DISABLE_COLOR)
                        )
                    }
                }
            }
        }
        Card (
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        viewModel.onToggleButtonInactive()
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(
                    if (!state.value.isCanvasSelected) AppConst.HOME_BACKGROUND_SECONDARY_COLOR else AppConst.HOME_BACKGROUND_PRIMARY_COLOR
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            if (!state.value.isCanvasSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.recent),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.recent),
                            color = Color(AppConst.TEXT_PRIMARY_COLOR)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.recent),
                            contentDescription = null,
                            tint = Color(AppConst.TEXT_DISABLE_COLOR)
                        )
                        Text(
                            text = stringResource(R.string.recent),
                            color = Color(AppConst.TEXT_DISABLE_COLOR)
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ToggleButtonPreview() {
    GoodNoteTheme {
        ToggleButton()
    }
}