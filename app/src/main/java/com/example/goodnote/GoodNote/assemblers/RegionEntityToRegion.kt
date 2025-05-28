package com.example.goodnote.goodNote.assemblers

import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.domain.Stroke
import com.example.goodnote.domain.toRegion
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.repository.RegionRepository
import com.example.goodnote.goodNote.repository.StrokeRepository
import kotlinx.coroutines.flow.first

suspend fun assembleRegion(
    regionEntity: RegionEntity,
    strokeRepository: StrokeRepository,
    regionRepository: RegionRepository,
): Region {
    val primaryStroke: Stroke? = regionEntity.primaryStrokeId?.let { it ->
        strokeRepository.selectStrokeWithId(it).first()
    }
    val subRegions: Map<String, Region?> = getSubRegion(
        listOf(
            regionEntity.topLeftRegionId,
            regionEntity.topRightRegionId,
            regionEntity.bottomLeftRegionId,
            regionEntity.bottomRightRegionId
        ),
        strokeRepository,
        regionRepository
    )
    return regionEntity.toRegion(
        primaryStroke,
        getOverlapsStrokes(regionEntity.overlapsStrokesId, strokeRepository),
        subRegions
    )
}

suspend fun getOverlapsStrokes(
    overlapsStrokesId: List<String>,
    strokeRepository: StrokeRepository,
): List<Stroke> {
    val overlapsStrokes: MutableList<Stroke> = mutableListOf()
    overlapsStrokesId.forEach { overlapsStrokesId ->
        val stroke: Stroke? = strokeRepository.selectStrokeWithId(overlapsStrokesId).first()
        if (stroke !== null)
            overlapsStrokes.add(stroke)
    }
    return overlapsStrokes
}

suspend fun getSubRegion(
    subRegions: List<String?>,
    strokeRepository: StrokeRepository,
    regionRepository: RegionRepository,
): Map<String, Region?> {
    val subRegionEntities: MutableMap<String, RegionEntity?> = mutableMapOf()
    val result: MutableMap<String, Region?> = mutableMapOf()

    return subRegions
        .filterNotNull()
        .associateWith { regionId ->
            val entity: RegionEntity? = regionRepository.selectRegionEntityWithId(regionId).first()
            entity?.let { regionEntity ->
                assembleRegion(
                    regionEntity,
                    strokeRepository,
                    regionRepository
                )
            }
        }
}