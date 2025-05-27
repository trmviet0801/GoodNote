package com.example.goodnote.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goodnote.domain.Page
import com.example.goodnote.domain.RegionEntity
import com.example.goodnote.domain.Stroke
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDAO {
    //Page
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertPage(page: Page)
    @Update
    suspend fun updatePage(page: Page)
    @Delete
    suspend fun deletePage(page: Page)
    @Query("SELECT * FROM pages WHERE id = :uuid")
    fun selectPageWithId(uuid: String): Flow<Page?>
    @Query("SELECT * FROM pages")
    fun selectAllPages(): Flow<List<Page?>>
}