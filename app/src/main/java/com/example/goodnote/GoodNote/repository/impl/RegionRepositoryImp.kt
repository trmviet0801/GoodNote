package com.example.goodnote.goodNote.repository.impl

import com.example.goodnote.database.dao.RegionDAO
import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.repository.RegionRepository
import kotlinx.coroutines.flow.Flow

class RegionRepositoryImp(
    private val regionDAO: RegionDAO
): RegionRepository {
    override suspend fun insertRegionEntity(regionEntity: RegionEntity) {
        regionDAO.insertRegionEntity(regionEntity)
    }

    override suspend fun deleteRegionEntity(regionEntity: RegionEntity) {
        regionDAO.deleteRegionEntity(regionEntity)
    }

    override fun selectRegionEntityWithId(uuid: String): Flow<RegionEntity?> {
        return regionDAO.selectRegionEntityWithId(uuid)
    }

    override suspend fun updateRegionEntity(regionEntity: RegionEntity) {
        regionDAO.updateRegionEntity(regionEntity)
    }

    override fun selectAllRegionEntities(): Flow<List<RegionEntity?>> {
        return regionDAO.selectAllRegionEntities()
    }

    override suspend fun insertRegionEntities(regionEntities: List<RegionEntity>) {
        regionDAO.insertRegionEntities(regionEntities)
    }

    override suspend fun updateRegionEntities(regionEntities: List<RegionEntity>) {
        regionDAO.updateRegionEntities(regionEntities)
    }
}