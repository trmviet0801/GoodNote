package com.example.goodnote.goodNote.presentation.editor

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.goodnote.R
import com.example.goodnote.goodNote.domain.toPath
import com.example.goodnote.goodNote.presentation.editor.components.BackgroundColorPicker
import com.example.goodnote.goodNote.presentation.editor.components.ImageDot
import com.example.goodnote.goodNote.presentation.editor.components.ImagePicker
import com.example.goodnote.goodNote.presentation.editor.components.PenPicker
import com.example.goodnote.goodNote.presentation.editor.components.PenSelection
import com.example.goodnote.goodNote.presentation.editor.components.TopBar
import com.example.goodnote.goodNote.utils.AppConvertor
import com.example.goodnote.goodNote.utils.PenConst
import com.example.goodnote.ui.theme.GoodNoteTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorScreen() {
    val editorViewModel = koinViewModel<EditorViewModel>()
    val state = editorViewModel.state.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var downTime = remember { mutableStateOf(0L) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF72A0C1))
            .onGloballyPositioned { layoutCoordinates ->
                editorViewModel.adjustPageSize(layoutCoordinates)
            }
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        TopBar(focusRequester)
        PenSelection()
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            this@Column.AnimatedVisibility(
                visible = state.value.isShowPenPicker,
                modifier = Modifier
                    .padding(16.dp)
                    .zIndex(1f)
                    .align(Alignment.TopCenter)
            ) {
                PenPicker()
            }

            if (state.value.isShowImagePicker)
                ImagePicker()
            if (state.value.isShowBackgroundColorPicker)
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).zIndex(2f)
                ) {
                    BackgroundColorPicker()
                }
            Canvas(
                modifier = Modifier
                    .pointerInteropFilter { motionEvent ->
                        //tapping detection
                        if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN)
                            downTime.value = System.currentTimeMillis()
                        if (motionEvent.actionMasked == MotionEvent.ACTION_UP)
                            if (System.currentTimeMillis() - downTime.value <= 200) {
                                editorViewModel.onTapHandle(
                                    _root_ide_package_.androidx.compose.ui.geometry.Offset(
                                        motionEvent.x,
                                        motionEvent.y
                                    )
                                )
                                true
                            }
                        //handle other behaviors
                        focusManager.clearFocus()
                        editorViewModel.handleInput(motionEvent)
                        true
                    }
                    .width(
                        AppConvertor.pxToDp(
                            state.value.rootRegion?.boundary?.actualWidth ?: 100f
                        ) * state.value.scale
                    )
                    .height(
                        AppConvertor.pxToDp(
                            state.value.rootRegion?.boundary?.actualHeight ?: 100f
                        ) * state.value.scale
                    )
                    .background(Color(state.value.backgroundColor))) {
                clipRect {
                    //draw images
                    state.value.imageManager.images.forEach { image ->
                        val imageBitMap: ImageBitmap =
                            editorViewModel.getImageBitmap(image.uri).asImageBitmap()
                        image.setSize(imageBitMap, state.value.scale)
                        drawImage(
                            image = imageBitMap,
                            dstOffset = image.getRelativePosition(
                                state.value.canvasRelativePosition,
                                state.value.scale
                            ),
                            dstSize = IntSize(
                                image.scaledWidth.toInt(),
                                image.scaledHeight.toInt()
                            )
                        )
                        //display circle-border when selecting image
                        if (image.isSelected) {
                            drawCircle(
                                color = Color.Gray,
                                radius = 10f,
                                center = image.getRelativePositionOffset(
                                    state.value.canvasRelativePosition,
                                    state.value.scale
                                )
                            )
                            drawCircle(
                                color = Color.Gray,
                                radius = 10f,
                                center = image.getRelativePositionOffset(
                                    state.value.canvasRelativePosition,
                                    state.value.scale
                                ) + _root_ide_package_.androidx.compose.ui.geometry.Offset(
                                    image.scaledWidth, 0f
                                )
                            )
                            drawCircle(
                                color = Color.Gray,
                                radius = 10f,
                                center = image.getRelativePositionOffset(
                                    state.value.canvasRelativePosition,
                                    state.value.scale
                                ) + Offset(0f, image.scaledHeight)
                            )
                            drawCircle(
                                color = Color.Gray,
                                radius = 10f,
                                center = image.getRelativePositionOffset(
                                    state.value.canvasRelativePosition,
                                    state.value.scale
                                ) + Offset(image.scaledWidth, image.scaledHeight)
                            )
                        }
                    }
                    drawPath(
                        path = state.value.latestStroke.toPath(
                            state.value.canvasRelativePosition
                        ),
                        color = Color(state.value.latestStroke.color),
                        style = Stroke(
                            state.value.latestStroke.lineWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    drawPath(
                        path = (state.value.rootRegion?.primaryStroke?.toPath(
                            state.value.canvasRelativePosition
                        )
                            ?: Path()),
                        color = Color(state.value.rootRegion?.primaryStroke?.color ?: 0xffffff),
                        style = Stroke(
                            state.value.rootRegion?.primaryStroke?.lineWidth
                                ?: PenConst.DEFAULT_LINE_WIDTH,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    state.value.rootRegion?.overlapsStrokes?.forEach { stroke ->
                        drawPath(
                            path = stroke.toPath(
                                state.value.canvasRelativePosition
                            ),
                            color = Color(stroke.color),
                            style = Stroke(
                                stroke.lineWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.fullscreen),
                    contentDescription = R.string.full_screen.toString(),
                    modifier = Modifier
                        .background(Color(0xFF2A2A2E), CircleShape)
                        .size(44.dp)
                        .padding(14.dp)
                        .align(Alignment.TopEnd)
                        .clickable(
                            enabled = true,
                            onClick = {
                                editorViewModel.onFullScreenChange()
                            }
                        )
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditorScreenPreview() {
    GoodNoteTheme {
        EditorScreen()
    }
}