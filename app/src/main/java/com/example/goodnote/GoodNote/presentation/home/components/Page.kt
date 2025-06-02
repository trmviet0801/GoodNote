package com.example.goodnote.goodNote.presentation.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goodnote.domain.Page
import com.example.goodnote.domain.Stroke
import com.example.goodnote.domain.timeStampToDateTime
import com.example.goodnote.domain.toPath
import com.example.goodnote.goodNote.presentation.home.HomeViewModel
import com.example.goodnote.goodNote.utils.AppConst
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Page(
    page: Page,
    navController: NavController
) {
    val viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
    val strokes = remember { mutableStateOf<List<Stroke>>(emptyList()) }

    LaunchedEffect(page.rootRegionId) {
        strokes.value = viewModel.getMiniStrokesOfPage(page.rootRegionId)
    }

    Column(
        modifier = Modifier
            .clickable(
                enabled = true,
                onClick = {
                    navController.navigate("editor/${page.id}")
                }
            )
            .width(250.dp)
            .padding(top = 16.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(page.backgroundColor))
        ) {
            clipRect {
                strokes.value.forEach { stroke ->
                    drawPath(
                        path = stroke.toPath(true),
                        color = Color(stroke.color),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            stroke.lineWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = page.name,
                    color = Color(AppConst.TEXT_PRIMARY_COLOR)
                )
                Text(
                    text = page.timeStampToDateTime(),
                    color = Color(AppConst.TEXT_PRIMARY_COLOR)
                )
            }
        }
    }
}