package com.example.goodnote.note.utils

import android.content.res.Resources
import androidx.compose.ui.unit.Dp

object AppConvertor {
    fun pxToDp(px: Float): Dp {
        return Dp(px / Resources.getSystem().displayMetrics.density)
    }
}