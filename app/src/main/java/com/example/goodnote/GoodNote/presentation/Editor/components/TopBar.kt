package com.example.goodnote.goodNote.presentation.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.goodnote.ui.theme.GoodNoteTheme
import com.example.goodnote.R
import com.example.goodnote.goodNote.presentation.editor.EditorViewModel
import com.example.goodnote.goodNote.utils.AppConst
import org.koin.androidx.compose.koinViewModel

@Composable
fun TopBar(
    focusRequester: FocusRequester,
    navController: NavController
) {
    val viewModel = koinViewModel<EditorViewModel>()
    val state = viewModel.state.collectAsState()

    AnimatedVisibility(
        visible = state.value.isFullScreen,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                        .clickable(
                            enabled = true,
                            onClick = {
                                navController.popBackStack()
                            }
                        ),
                    tint = Color.White
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
                    imageVector = ImageVector.vectorResource(R.drawable.add),
                    contentDescription = R.string.add.toString(),
                    modifier = Modifier
                        .size(AppConst.ICON_SIZE)
                        .clickable(
                            enabled = true,
                            onClick = {
                                viewModel.showImagePicker()
                            }
                        ),
                    tint = Color.White
                )
                Box() {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable._dots),
                        contentDescription = R.string.dots.toString(),
                        modifier = Modifier
                            .size(AppConst.ICON_SIZE)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    viewModel.onDropDownMenu()
                                }
                            ),
                        tint = Color.White
                    )
                    DropdownMenu(
                        expanded = state.value.isShowSettingPopupMenu,
                        onDismissRequest = { viewModel.onDropDownMenu() }
                    ) {
                        DropdownMenuItem(onClick = {
                            viewModel.onDropDownMenu()
                            viewModel.onShowBackgroundColorPicker()
                        }, text = { Text("Background color") })
                    }
                }
            }
        }
    }
}

//@PreviewLightDark
//@Composable
//private fun TopBarPreview() {
//    GoodNoteTheme {
//        TopBar(FocusRequester())
//    }
//}