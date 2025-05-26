package com.example.goodnote.goodNote.domain

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import java.sql.Timestamp

//left + top only used for checking if user touch image
//actualTop + actualLeft -> image position
data class Image(
    val uri: Uri,
    var left: Float = 300f,
    var top: Float = 300f,
    var width: Float = 0f,
    var height: Float = 0f,
    var scaledWidth: Float = 0f,
    var scaledHeight: Float = 0f,
    var actualTop: Float = 0f,
    var actualLeft: Float = 0f,
    var isSelected: Boolean = false,
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis())
) {
    // called when new image is inserted
    fun setActualPosition(screenPosition: Offset, scale: Float): Image {
        return this.copy(
            actualLeft = screenPosition.x + left * scale,
            actualTop = screenPosition.y + top * scale
        )
    }

    fun setSize(imageBitMap: ImageBitmap, scale: Float) {
        if (this.width == 0f && this.height == 0f) {
            this.width = imageBitMap.width.toFloat()
            this.height = imageBitMap.height.toFloat()
        }
        this.scaledWidth = this.width * scale
        this.scaledHeight = this.height * scale
    }

    fun getRelativePosition(virtualCameraOffset: Offset, scale: Float = 1f): IntOffset {
        return IntOffset(
            (actualLeft * scale - virtualCameraOffset.x).toInt(),
            (actualTop * scale - virtualCameraOffset.y).toInt()
        )
    }

    fun getRelativePositionOffset(virtualCameraOffset: Offset, scale: Float = 1f): Offset {
        return Offset(
            (actualLeft * scale - virtualCameraOffset.x),
            (actualTop * scale - virtualCameraOffset.y)
        )
    }

    fun onImage(offset: Offset,virtualCameraOffset: Offset ,scale: Float): Boolean {
        val currentPosition: IntOffset = getRelativePosition(virtualCameraOffset, scale)
        return (
                offset.x >= currentPosition.x && offset.x <= currentPosition.x + scaledWidth
                ) &&
                (
                        offset.y >= currentPosition.y && offset.y <= currentPosition.y + scaledHeight
                        )
    }

    fun onTap(): Image {
        return this.copy(
            isSelected = !this.isSelected
        )
    }

    fun isEquals(image: Image): Boolean {
        return this.uri == image.uri && this.timestamp == image.timestamp
    }

    fun onClick(): Image {
        return this.copy(
            isSelected = !this.isSelected
        )
    }
}