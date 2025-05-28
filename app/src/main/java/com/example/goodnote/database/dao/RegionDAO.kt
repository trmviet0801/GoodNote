package com.example.goodnote.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goodnote.domain.RegionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionDAO {
    //Region
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegionEntities(regionEntities: List<RegionEntity>)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertRegionEntity(regionEntity: RegionEntity)
    @Update
    suspend fun updateRegionEntities(regionEntities: List<RegionEntity>)
    @Update
    suspend fun updateRegionEntity(regionEntity: RegionEntity)
    @Delete
    suspend fun deleteRegionEntity(regionEntity: RegionEntity)
    @Query("SELECT * FROM regions WHERE regionId = :uuid")
    fun selectRegionEntityWithId(uuid: String): Flow<RegionEntity?>
    @Query("SELECT * FROM regions")
    fun selectAllRegionEntities(): Flow<List<RegionEntity?>>
}