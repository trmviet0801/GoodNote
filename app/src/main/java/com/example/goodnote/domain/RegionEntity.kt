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
    var topLeftRegion: String? = null,
    var topRightRegion: String? = null,
    var bottomLeftRegion: String? = null,
    var bottomRightRegion: String? = null
)
fun RegionEntity.toRegion(): Region {
    return Region(
        regionId = this.regionId!!,
        primaryStroke = null,
        boundary = this.boundary,
        overlapsStrokes = emptyList(),
        isDivided = this.isDivided,
        isRoot = this.isRoot,
        topLeftRegion = null,
        topRightRegion = null,
        bottomLeftRegion = null,
        bottomRightRegion = null
    )
}

fun Region.toEntity(): RegionEntity {
    return RegionEntity(
        regionId = this.regionId,
        primaryStrokeId = this.primaryStroke?.strokeId,
        boundary = this.boundary,
        overlapsStrokesId = this.overlapsStrokes.map { stroke -> stroke.strokeId },
        isDivided = this.isDivided,
        isRoot = this.isRoot,
        topLeftRegion = this.topLeftRegion?.regionId,
        topRightRegion = this.topRightRegion?.regionId,
        bottomLeftRegion = this.bottomLeftRegion?.regionId,
        bottomRightRegion = this.bottomRightRegion?.regionId
    )
}