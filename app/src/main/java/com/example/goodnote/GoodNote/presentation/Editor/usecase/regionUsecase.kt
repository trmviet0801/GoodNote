package com.example.goodnote.goodNote.presentation.editor.usecase

import com.example.goodnote.domain.toEntity
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository

suspend fun saveRegion(
    region: Region,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    regionRepository.insertRegionEntity(region.toEntity())
    saveStrokesOfRegion(region, strokeRepository)
}

suspend fun saveRegions(
    regions: List<Region>,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    regionRepository.insertRegionEntities(regions.map { it.toEntity() })
    regions.forEach { region ->
        saveStrokesOfRegion(region, strokeRepository)
    }
}

suspend fun updateRegion(
    region: Region,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    regionRepository.insertRegionEntity(region.toEntity())
    updateStrokesOfRegion(region, strokeRepository)
}

suspend fun updateRegions(
    regions: List<Region>,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    regionRepository.insertRegionEntities(regions.map { it.toEntity() })
    regions.forEach { region ->
        updateStrokesOfRegion(region, strokeRepository)
    }
}

suspend fun updateRegions(
    regions: List<Region>,
    regionRepository: RegionRepository
){
    regionRepository.updateRegionEntities(regions.map { it.toEntity() })
}

suspend fun saveRootRegion(
    region: Region,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    saveRegion(region, regionRepository, strokeRepository)
    if (region.topLeftRegion != null) saveRootRegion(region.topLeftRegion!!, regionRepository, strokeRepository)
    if (region.topRightRegion != null) saveRootRegion(region.topRightRegion!!, regionRepository, strokeRepository)
    if (region.bottomLeftRegion != null) saveRootRegion(region.bottomLeftRegion!!, regionRepository, strokeRepository)
}

suspend fun updateRootRegion(
    region: Region,
    regionRepository: RegionRepository,
    strokeRepository: StrokeRepository
) {
    updateRegion(region, regionRepository, strokeRepository)
    if (region.topLeftRegion != null) updateRootRegion(region.topLeftRegion!!, regionRepository, strokeRepository)
    if (region.topRightRegion != null) updateRootRegion(region.topRightRegion!!, regionRepository, strokeRepository)
    if (region.bottomLeftRegion != null) updateRootRegion(region.bottomLeftRegion!!, regionRepository, strokeRepository)

}