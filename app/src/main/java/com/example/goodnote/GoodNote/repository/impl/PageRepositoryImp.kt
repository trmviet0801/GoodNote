package com.example.goodnote.goodNote.repository.impl

import com.example.goodnote.database.dao.PageDAO
import com.example.goodnote.domain.Page
import com.example.goodnote.goodNote.repository.PageRepository
import kotlinx.coroutines.flow.Flow

class PageRepositoryImp(
    private val pageDAO: PageDAO
): PageRepository {
    override suspend fun insertPage(page: Page) {
        pageDAO.insertPage(page)
    }

    override suspend fun updatePage(page: Page) {
        pageDAO.updatePage(page)
    }

    override suspend fun deletePage(page: Page) {
        pageDAO.deletePage(page)
    }

    override fun selectPageWithId(uuid: String): Flow<Page?> {
        return pageDAO.selectPageWithId(uuid)
    }

    override fun selectAllPagesOrderByName(): Flow<List<Page?>> {
        return pageDAO.selectAllPagesOrderByName()
    }

    override fun selectAllPagesOrderByLatestTimeStamp(): Flow<List<Page?>> {
        return pageDAO.selectAllPagesOrderByLatestTimeStamp()
    }
}