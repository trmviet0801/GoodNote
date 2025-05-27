package com.example.goodnote.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goodnote.domain.Page
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PageDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
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