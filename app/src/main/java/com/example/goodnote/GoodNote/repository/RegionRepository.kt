package com.example.goodnote.goodNote.repository

import com.example.goodnote.domain.Page
import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.goodNote.domain.Region
import kotlinx.coroutines.flow.Flow

interface RegionRepository {
    suspend fun insertRegionEntities(regionEntities: List<RegionEntity>)
    suspend fun insertRegionEntity(regionEntity: RegionEntity)
    suspend fun deleteRegionEntity(regionEntity: RegionEntity)
    fun selectRegionEntityWithId(uuid: String): Flow<RegionEntity?>
    suspend fun updateRegionEntity(regionEntity: RegionEntity)
    suspend fun updateRegionEntities(regionEntities: List<RegionEntity>)
    fun selectAllRegionEntities(): Flow<List<RegionEntity?>>
}