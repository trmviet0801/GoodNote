package com.example.goodnote.goodNote.repository

import com.example.goodnote.domain.Page
import com.example.goodnote.domain.RegionEntity
import kotlinx.coroutines.flow.Flow

interface RegionRepository {
    suspend fun insertRegionEntity(regionEntity: RegionEntity)
    suspend fun deleteRegionEntity(regionEntity: RegionEntity)
    fun selectRegionEntityWithId(uuid: String): Flow<RegionEntity?>
    suspend fun updateRegionEntity(regionEntity: RegionEntity)
    fun selectAllRegionEntities(): Flow<List<RegionEntity?>>
}