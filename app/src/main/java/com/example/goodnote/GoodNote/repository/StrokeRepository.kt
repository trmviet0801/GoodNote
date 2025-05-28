package com.example.goodnote.goodNote.repository

import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.domain.Stroke
import kotlinx.coroutines.flow.Flow

interface StrokeRepository {
    suspend fun insertStrokes(strokes: List<Stroke>)
    suspend fun insertStroke(stroke: Stroke)
    suspend fun deleteStroke(stroke: Stroke)
    suspend fun updateStroke(stroke: Stroke)
    suspend fun updateStrokes(strokes: List<Stroke>)
    fun selectStrokeWithId(uuid: String): Flow<Stroke?>
}