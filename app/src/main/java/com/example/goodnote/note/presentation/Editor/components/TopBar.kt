package com.example.goodnote.note.presentation.Editor.components

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.goodnote.ui.theme.GoodNoteTheme
import com.example.goodnote.R
import com.example.goodnote.note.presentation.Editor.EditorViewModel
import com.example.goodnote.note.utils.AppConst
import org.koin.androidx.compose.koinViewModel

@Composable
fun TopBar(focusRequester: FocusRequester) {
    val viewModel = koinViewModel<EditorViewModel>()
    val state = viewModel.state.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(8.dp)
            .zIndex(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                contentDescription = R.string.arrow_left.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE)
                    .padding(4.dp)
            )
            BasicTextField(
                value = state.value.name,
                onValueChange = { viewModel.onTitleChange(it) },
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.fullscreen),
                contentDescription = R.string.full_screen.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE)
                    .padding(4.dp)
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.add),
                contentDescription = R.string.add.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE)
                //.padding(4.dp)
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable._dots),
                contentDescription = R.string.dots.toString(),
                modifier = Modifier
                    .size(AppConst.ICON_SIZE)
                //.padding(4.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TopBarPreview() {
    GoodNoteTheme {
        TopBar(FocusRequester())
    }
}