package com.example.goodnote.goodNote.navigation

sealed class Route(val route: String) {
    object Home: Route("home")
    object Editor: Route("editor/{pageId}") {
        fun loadPage(pageId: String) = "editor/$pageId"
    }
}