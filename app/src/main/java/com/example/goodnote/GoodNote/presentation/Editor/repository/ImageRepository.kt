package com.example.goodnote.goodNote.presentation.editor.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

class ImageRepository(
    private val context: Context
) {
    private val imageBitmap: MutableMap<Uri, Bitmap> = mutableMapOf()

    fun getImageBitmap(uri: Uri): Bitmap {
        return imageBitmap.getOrPut(uri) {
            context.contentResolver.openInputStream(uri).use { input ->
                BitmapFactory.decodeStream(input)
            }
        }
    }

    fun clearCache() {
        imageBitmap.clear()
    }
}