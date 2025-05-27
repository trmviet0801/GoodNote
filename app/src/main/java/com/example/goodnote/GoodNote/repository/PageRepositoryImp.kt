package com.example.goodnote.goodNote.repository

import com.example.goodnote.database.PageDAO
import com.example.goodnote.domain.Page
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

    override fun selectAllPages(): Flow<List<Page?>> {
        return pageDAO.selectAllPages()
    }
}