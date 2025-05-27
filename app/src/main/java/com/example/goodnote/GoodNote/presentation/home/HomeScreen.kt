package com.example.goodnote.goodNote.presentation.home

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.goodnote.goodNote.presentation.home.components.AddPage
import com.example.goodnote.goodNote.presentation.home.components.Page
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeViewModel: HomeViewModel = koinViewModel<HomeViewModel>()
    val state = homeViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.getAllPages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF72A0C1))
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        AddPage()
        Spacer(modifier = Modifier.height(50.dp))
        state.value.pages.forEach { page ->
            Page(page.name, page.id, navController)
        }
    }
}
