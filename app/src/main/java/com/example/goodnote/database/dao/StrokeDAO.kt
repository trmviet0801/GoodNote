package com.example.goodnote.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goodnote.domain.Stroke
import kotlinx.coroutines.flow.Flow

@Dao
interface StrokeDAO {
    //Stroke
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrokes(strokes: List<Stroke>)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertStroke(stroke: Stroke)
    @Delete
    suspend fun deleteStroke(stroke: Stroke)
    @Query("SELECT * FROM strokes WHERE strokeId = :uuid")
    fun selectStrokeWithId(uuid: String): Flow<Stroke?>
    @Update
    suspend fun updateStroke(stroke: Stroke)
    @Update
    suspend fun updateStrokes(strokes: List<Stroke>)
}