package com.example.goodnote.goodNote.repository

import com.example.goodnote.domain.Page
import kotlinx.coroutines.flow.Flow

interface PageRepository {
    suspend fun insertPage(page: Page)
    suspend fun updatePage(page: Page)
    suspend fun deletePage(page: Page)
    fun selectPageWithId(uuid: String): Flow<Page?>
    fun selectAllPagesOrderByName(): Flow<List<Page?>>
    fun selectAllPagesOrderByLatestTimeStamp(): Flow<List<Page?>>
}