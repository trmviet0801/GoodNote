package com.example.goodnote.utils

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import com.example.goodnote.goodNote.domain.Image
import com.example.goodnote.goodNote.domain.ImageManager
import com.example.goodnote.goodNote.domain.Region
import com.example.goodnote.goodNote.domain.Stroke
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PageTypeConverters {
    private val gson = Gson()
    @TypeConverter
    fun fromRootRegion(rootRegion: Region): String {
        return gson.toJson(rootRegion)
    }
    @TypeConverter
    fun toRootRegion(json: String): Region {
        return gson.fromJson(json, Region::class.java)
    }
    @TypeConverter
    fun fromOffsetList(offsets: List<Offset>): String {
        return gson.toJson(offsets.map { listOf(it.x, it.y) })
    }

    @TypeConverter
    fun toOffsetList(json: String): List<Offset> {
        val type = object : TypeToken<List<List<Float>>>() {}.type
        val rawList: List<List<Float>> = gson.fromJson(json, type)
        return rawList.map { Offset(it[0], it[1]) }
    }
    @TypeConverter
    fun fromStrokes(oversizeStrokes: List<Stroke>): String {
        return gson.toJson(oversizeStrokes)
    }
    @TypeConverter
    fun toStrokes(json: String): List<Stroke> {
        val type: Type = object : TypeToken<List<Stroke>>() {}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun fromImageManager(imageManager: ImageManager): String {
        return gson.toJson(imageManager)
    }
    @TypeConverter
    fun toImageManager(json: String): ImageManager {
        return gson.fromJson(json, ImageManager::class.java)
    }
//    @TypeConverter
//    fun fromColor(color: Color): String {
//        return gson.toJson(color)
//    }
//    @TypeConverter
//    fun toColor(json: String): Color {
//        return gson.fromJson(json, Color::class.java)
//    }
    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }
    @TypeConverter
    fun toUri(json: String): Uri {
        return Uri.parse(json)
    }
    @TypeConverter
    fun fromImages(images: List<Image>): String {
        val type: Type = object : TypeToken<List<Image>>(){}.type
        return gson.toJson(images, type)
    }
    @TypeConverter
    fun toImages(json: String): List<Image> {
        return gson.fromJson(json, Array<Image>::class.java).toList()
    }
    @TypeConverter
    fun fromOffset(offset: Offset): String {
        // Serialize as JSON array [x, y]
        return gson.toJson(listOf(offset.x, offset.y))
    }

    @TypeConverter
    fun toOffset(json: String): Offset {
        val list: List<Float> = gson.fromJson(json, object : TypeToken<List<Float>>() {}.type)
        return Offset(list[0], list[1])
    }
}