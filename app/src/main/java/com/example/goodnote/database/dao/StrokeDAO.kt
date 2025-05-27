package com.example.goodnote.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goodnote.domain.Stroke
import kotlinx.coroutines.flow.Flow

@Dao
interface StrokeDAO {
    //Stroke
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertStroke(stroke: Stroke)
    @Delete
    suspend fun deleteStroke(stroke: Stroke)
    @Query("SELECT * FROM strokes WHERE strokeId = :uuid")
    fun selectStrokeWithId(uuid: String): Flow<Stroke?>
}