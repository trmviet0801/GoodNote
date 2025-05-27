package com.example.goodnote.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
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
    fun fromOffset(size: List<Offset>): String {
        return gson.toJson(size)
    }
    @TypeConverter
    fun toOffset(json: String): List<Offset> {
        val type: Type = object : TypeToken<List<Offset>>() {}.type
        return gson.fromJson(json, type)
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
    @TypeConverter
    fun fromColor(color: Offset): String {
        return gson.toJson(color)
    }
    @TypeConverter
    fun toColor(json: String): Color {
        return gson.fromJson(json, Color::class.java)
    }
}