package com.example.goodnote.goodNote.presentation.home

import android.util.Log
import android.view.WindowInsetsController
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goodnote.goodNote.presentation.home.components.AddPage
import com.example.goodnote.goodNote.presentation.home.components.Canvases
import com.example.goodnote.goodNote.presentation.home.components.Page
import com.example.goodnote.goodNote.presentation.home.components.TopBar
import com.example.goodnote.goodNote.utils.AppConst
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeViewModel: HomeViewModel = koinViewModel<HomeViewModel>()
    val state = homeViewModel.state.collectAsState()

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    val backgroundColor = Color(AppConst.HOME_BACKGROUND_SECONDARY_COLOR)

    LaunchedEffect(Unit) {
        homeViewModel.getAllPages()
        systemUiController.setSystemBarsColor(
            backgroundColor,
            useDarkIcons
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(AppConst.HOME_BACKGROUND_PRIMARY_COLOR))
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        TopBar()
        Canvases(navController)
    }
}
