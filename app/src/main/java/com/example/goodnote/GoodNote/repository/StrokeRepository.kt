package com.example.goodnote.goodNote.repository

import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.domain.Stroke
import kotlinx.coroutines.flow.Flow

interface StrokeRepository {
    suspend fun insertStroke(stroke: Stroke)
    suspend fun deleteStroke(stroke: Stroke)
    fun selectStrokeWithId(uuid: String): Flow<Stroke?>
}