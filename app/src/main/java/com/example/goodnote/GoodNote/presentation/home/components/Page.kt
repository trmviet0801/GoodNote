package com.example.goodnote.goodNote.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun Page(
    name: String,
    pageId: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .clickable(
                enabled = true,
                onClick = {
                    navController.navigate("editor/$pageId")
                }
            )
    ) {
        Text(name)
    }
}