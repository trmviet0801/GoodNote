package com.example.goodnote.goodNote.utils

import androidx.compose.ui.unit.dp

object AppConst {
    const val PAGE_NAME: String = "New file"
    const val SCALE_LEVEL: Float = 0.003f
    const val SCROLL_LEVEL: Float = 40f
    const val SCROLL_MINIMUM: Float = 2.5f
    const val SCROLL_AXIS_DOMINANT = 2
    const val MIN_FREE_SPACE = 500f
    val ICON_SIZE = 24.dp
    val BARs_HEIGHT = 50.dp
    val PEN_COLORS = listOf(
        0xFF000000, // Black
        0xFFFFFFFF, // White
        0xFFFF0000, // Red
        0xFF8B0000, // Dark Red
        0xFFFFC0CB, // Pink
        0xFFFF69B4, // Hot Pink
        0xFF0000FF, // Blue
        0xFF00008B, // Dark Blue
        0xFFADD8E6, // Light Blue
        0xFF87CEEB, // Sky Blue
        0xFF1E90FF, // Dodger Blue
        0xFF4682B4, // Steel Blue
        0xFF800080, // Purple
        0xFFE6E6FA, // Lavender
        0xFFDA70D6, // Orchid
        0xFFBA55D3, // Medium Orchid
        0xFF008000, // Green
        0xFF90EE90, // Light Green
        0xFF006400, // Dark Green
        0xFF2E8B57, // Sea Green
        0xFF3CB371, // Medium Sea Green
        0xFFFFFF00, // Yellow
        0xFFFFD700, // Golden Yellow
        0xFFFFA500, // Orange
        0xFFFF8C00, // Dark Orange
        0xFFFFE4B5, // Moccasin
        0xFFFFDEAD, // Navajo White
        0xFF808080, // Gray
        0xFF696969, // Dark Gray
        0xFFA9A9A9, // Dim Gray
        0xFFB0C4DE, // Light Steel Blue
        0xFFD3D3D3, // Light Gray
        0xFFB22222, // Firebrick
        0xFF5F9EA0, // Cadet Blue
        0xFF6495ED, // Cornflower Blue
        0xFFA0522D, // Sienna
        0xFFCD853F, // Peru
        0xFFA52A2A, // Brown
        0xFFDEB887, // BurlyWood
        0xFFF5DEB3, // Wheat
        0xFFFFD1DC, // Pastel Pink
        0xFFFFFACD, // Pastel Yellow
        0xFF77DD77, // Pastel Green
        0xFFFFB347, // Pastel Orange
        0xFFB19CD9, // Pastel Purple
        0xFFFF6961, // Pastel Red
        0xFFCB99C9  // Light Mauve
    )
    const val PEN_WIDTH_LEVEL_MIN = 0
    const val PEN_WIDTH_LEVEL_MAX = 7
    const val PEN_WIDTH_MOVE_UNIT = 0.1f
}