package com.example.goodnote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goodnote.database.dao.PageDAO
import com.example.goodnote.database.dao.RegionDAO
import com.example.goodnote.database.dao.StrokeDAO
import com.example.goodnote.domain.Page
import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.domain.Stroke
import com.example.goodnote.utils.PageTypeConverters

@Database(entities = [Page::class, Stroke::class, RegionEntity::class], version = 12)
@TypeConverters(PageTypeConverters::class)
abstract class LocalDatabase: RoomDatabase() {
    abstract fun pageDao(): PageDAO
    abstract fun strokeDao(): StrokeDAO
    abstract fun regionDao(): RegionDAO
    companion object {
        //Instance is the reference to the database whenever it is created
        //Volatile means Instance is never cache -> always up-to-date
        @Volatile
        private var Instance: LocalDatabase? = null
        fun getDatabase(context: Context): LocalDatabase {
            //if Instance == null -> creating new db -> ensure no race condition
            return Instance ?: synchronized (this) {
                Room.databaseBuilder(
                    context,
                    LocalDatabase::class.java,
                    "page_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}