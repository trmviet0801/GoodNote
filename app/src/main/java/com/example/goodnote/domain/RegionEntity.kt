package com.example.goodnote.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.goodnote.goodNote.domain.Boundary
import com.example.goodnote.goodNote.domain.Region
import java.util.UUID

@Entity(tableName = "regions")
data class RegionEntity(
    @PrimaryKey
    var regionId: String = UUID.randomUUID().toString(),
    var primaryStrokeId: String? = null,
    var boundary: Boundary? = null,
    var overlapsStrokesId: List<String> = emptyList(),
    var isDivided: Boolean = false,
    var isRoot: Boolean = false,
    var topLeftRegionId: String? = null,
    var topRightRegionId: String? = null,
    var bottomLeftRegionId: String? = null,
    var bottomRightRegionId: String? = null
)

fun Region.toEntity(): RegionEntity {
    return RegionEntity(
        regionId = this.regionId,
        primaryStrokeId = this.primaryStroke?.strokeId,
        boundary = this.boundary,
        overlapsStrokesId = this.overlapsStrokes.map { stroke -> stroke.strokeId },
        isDivided = this.isDivided,
        isRoot = this.isRoot,
        topLeftRegionId = this.topLeftRegion?.regionId,
        topRightRegionId = this.topRightRegion?.regionId,
        bottomLeftRegionId = this.bottomLeftRegion?.regionId,
        bottomRightRegionId = this.bottomRightRegion?.regionId
    )
}

fun RegionEntity.toRegion(
    primaryStroke: Stroke?,
    overlapsStrokes: List<Stroke>,
    subRegions: Map<String, Region?>
): Region {
    return Region(
        regionId = this.regionId,
        primaryStroke = primaryStroke,
        boundary = this.boundary,
        overlapsStrokes = overlapsStrokes,
        isDivided = this.isDivided,
        isRoot = this.isRoot,
        topLeftRegion = subRegions[this.topLeftRegionId],
        topRightRegion = subRegions[this.topRightRegionId],
        bottomLeftRegion = subRegions[this.bottomLeftRegionId],
        bottomRightRegion = subRegions[this.bottomRightRegionId]
    )
}