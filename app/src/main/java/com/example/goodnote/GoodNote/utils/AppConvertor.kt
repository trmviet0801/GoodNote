package com.example.goodnote.goodNote.utils

import android.content.res.Resources
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

object AppConvertor {
    fun pxToDp(px: Float): Dp {
        return Dp(px / Resources.getSystem().displayMetrics.density)
    }
    fun convertOffset(offset: Offset): Offset {
        return Offset(abs(offset.x), abs(offset.y))
    }
}