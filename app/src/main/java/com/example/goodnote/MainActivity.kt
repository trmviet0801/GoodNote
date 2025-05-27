package com.example.goodnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.goodnote.goodNote.navigation.Route
import com.example.goodnote.goodNote.presentation.editor.EditorScreen
import com.example.goodnote.goodNote.presentation.home.HomeScreen
import com.example.goodnote.ui.theme.GoodNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodNoteTheme {
                val navController: NavHostController = rememberNavController()
                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home.route
                    ) {
                        composable (Route.Home.route) {
                            HomeScreen(navController)
                        }
                        composable(
                            route = Route.Editor.route,
                            arguments = listOf(navArgument("pageId") { type = NavType.StringType })
                        ) {
                            EditorScreen(navController, it.arguments?.getString("pageId") ?: "")
                        }
                    }
                }
            }
        }
    }
}