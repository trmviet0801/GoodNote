package com.example.goodnote.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goodnote.domain.Page
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDAO {
    //Page
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPage(page: Page)
    @Update
    suspend fun updatePage(page: Page)
    @Delete
    suspend fun deletePage(page: Page)
    @Query("SELECT * FROM pages WHERE id = :uuid")
    fun selectPageWithId(uuid: String): Flow<Page?>
    @Query("SELECT * FROM pages ORDER BY name ASC")
    fun selectAllPagesOrderByName(): Flow<List<Page?>>
    @Query("SELECT * FROM pages ORDER BY latestTimeStamp DESC")
    fun selectAllPagesOrderByLatestTimeStamp(): Flow<List<Page?>>
    @Query("SELECT * FROM pages WHERE name LIKE '%' || :keyword || '%'")
    fun selectPageWithKeyword(keyword: String): Flow<List<Page?>>
}