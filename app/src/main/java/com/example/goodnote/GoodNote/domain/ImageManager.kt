package com.example.goodnote.goodNote.domain

import android.app.Application
import android.util.Log
import androidx.compose.ui.geometry.Offset

data class ImageManager (
    val images: List<Image>
) {
    fun insertImage(image: Image): ImageManager {
        return this.copy(
            images = images + image
        )
    }

    fun removeImage(image: Image): ImageManager {
        val newImages: List<Image> = images.filter { it -> !it.isEquals(image) }
        return this.copy(
            images = newImages
        )
    }

    fun onSelectImage(image: Image): ImageManager {
        val newImage: List<Image> = images.map { it ->
            if (it.isEquals(image))
                it.isSelected = !it.isSelected
            else
                it.isSelected = false
            it
        }
        return this.copy(
            images = newImage
        )
    }

    //image changed position
    //only return new imageManager to trigger re-render
    fun onMoveImage(image: Image): ImageManager {
        return this.copy(
            images = this.images.map { it ->
                if (it.isEquals(image)) image else it
            }
        )
    }

    fun onTapHandle(tapPosition: Offset, virtualCameraOffset: Offset, scale: Float): ImageManager {
        val newImages: List<Image> = images.map { it ->
            if (it.onImage(tapPosition, virtualCameraOffset  ,scale))
                it.onTap()
            else
                it
        }
        return this.copy(
            images = newImages
        )
    }

    fun isTouchOnImage(position: Offset, virtualCameraOffset: Offset, scale: Float): Image? {
        images.forEach { image ->
            if (image.onImage(position, virtualCameraOffset, scale))
                return image
        }
        return null
    }

    //act as a middle layer to trigger re-render
    fun onScrolling(image: Image): ImageManager {
        return this.copy(
            images = this.images.map { it ->
                if (it.isEquals(image)) image else it
            }
        )
    }
}