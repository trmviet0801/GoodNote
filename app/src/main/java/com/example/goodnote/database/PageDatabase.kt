package com.example.goodnote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goodnote.domain.Page
import com.example.goodnote.utils.PageTypeConverters

@Database(entities = [Page::class], version = 1)
@TypeConverters(PageTypeConverters::class)
abstract class PageDatabase: RoomDatabase() {
    abstract fun pageDao(): PageDAO
    companion object {
        //Instance is the reference to the database whenever it is created
        //Volatile means Instance is never cache -> always up-to-date
        @Volatile
        private var Instance: PageDatabase? = null
        fun getDatabase(context: Context): PageDatabase {
            //if Instance == null -> creating new db -> ensure no race condition
            return Instance ?: synchronized (this) {
                Room.databaseBuilder(
                    context,
                    PageDatabase::class.java,
                    "page_database"
                ).build().also { Instance = it }
            }
        }
    }
}