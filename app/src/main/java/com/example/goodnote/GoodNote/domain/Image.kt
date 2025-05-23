package com.example.goodnote.goodNote.domain

import android.net.Uri

data class Image(
    val uri: Uri,
    var left: Float = 100f,
    var top: Float = 100f,
    var isSelected: Boolean = false,

)