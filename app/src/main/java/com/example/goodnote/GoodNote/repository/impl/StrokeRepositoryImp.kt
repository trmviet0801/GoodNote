package com.example.goodnote.goodNote.repository.impl

import com.example.goodnote.database.dao.PageDAO
import com.example.goodnote.database.dao.StrokeDAO
import com.example.goodnote.domain.Stroke
import com.example.goodnote.goodNote.repository.StrokeRepository
import kotlinx.coroutines.flow.Flow

class StrokeRepositoryImp(
    private val strokeDAO: StrokeDAO
): StrokeRepository {
    override suspend fun insertStroke(stroke: Stroke) {
        strokeDAO.insertStroke(stroke)
    }

    override suspend fun deleteStroke(stroke: Stroke) {
        strokeDAO.deleteStroke(stroke)
    }

    override fun selectStrokeWithId(uuid: String): Flow<Stroke?> {
        return strokeDAO.selectStrokeWithId(uuid)
    }

    override suspend fun insertStrokes(strokes: List<Stroke>) {
        strokeDAO.insertStrokes(strokes)
    }

    override suspend fun updateStroke(stroke: Stroke) {
        strokeDAO.updateStroke(stroke)
    }

    override suspend fun updateStrokes(strokes: List<Stroke>) {
        strokeDAO.updateStrokes(strokes)
    }
}