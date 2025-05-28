package com.example.goodnote.goodNote.presentation.editor.usecase

import android.util.Log
import com.example.goodnote.domain.Stroke
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.repository.StrokeRepository

suspend fun saveStroke(
    stroke: Stroke,
    strokeRepository: StrokeRepository
) {
    strokeRepository.insertStroke(stroke)
}

suspend fun saveStrokesOfRegion(
    region: Region,
    strokeRepository: StrokeRepository
) {
    if (region.primaryStroke !== null)
        strokeRepository.insertStroke(region.primaryStroke!!)
    strokeRepository.insertStrokes(region.overlapsStrokes)
}

suspend fun saveStrokes(
    stroke: List<Stroke>,
    strokeRepository: StrokeRepository
) {
    strokeRepository.insertStrokes(stroke)
}

suspend fun removeStroke(
    stroke: Stroke,
    strokeRepository: StrokeRepository
) {
    strokeRepository.deleteStroke(stroke)
}

suspend fun updateStrokesOfRegion(
    region: Region,
    strokeRepository: StrokeRepository
) {
    if (region.primaryStroke !== null)
        strokeRepository.insertStroke(region.primaryStroke!!)
    strokeRepository.insertStrokes(region.overlapsStrokes)
}