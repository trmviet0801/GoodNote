package com.example.goodnote.goodNote.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.home.HomeViewModel
import com.example.goodnote.goodNote.utils.AppConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchBox(
    focusRequester: FocusRequester
) {
    val viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state.value.isShowSearchBox) {
        if (state.value.isShowSearchBox) focusRequester.requestFocus()
    }

    Row (
        modifier = Modifier
            .background(Color(AppConst.HOME_BACKGROUND_PRIMARY_COLOR),
                shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        AnimatedVisibility(
            visible = state.value.isShowSearchBox
        ) {
            BasicTextField(
                value = state.value.keyword ?: "",
                onValueChange = { viewModel.onSearchKeywordChange(it) },
                textStyle = LocalTextStyle.current.copy(
                    color = Color(AppConst.TEXT_PRIMARY_COLOR),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
            )
        }
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = stringResource(R.string.search),
            modifier = Modifier
                .size(18.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        viewModel.onSearchBoxTap()
                    }
                )
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchBoxPreview() {
    GoodNoteTheme {
        //SearchBox()
    }
}