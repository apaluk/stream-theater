package com.apaluk.streamtheater.data.database.repository

import com.apaluk.streamtheater.data.database.dao.SearchHistoryDao
import com.apaluk.streamtheater.data.database.model.SearchHistory
import com.apaluk.streamtheater.domain.model.search.SearchHistoryItem
import com.apaluk.streamtheater.domain.model.search.util.toSearchHistoryItem
import com.apaluk.streamtheater.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
): SearchHistoryRepository {
    override fun getFilteredHistory(filter: String): Flow<List<SearchHistoryItem>> =
        searchHistoryDao.filterSearchHistory(filter).map { list ->
            list.map { it.toSearchHistoryItem() }
        }

    override suspend fun addToHistory(input: String) {
        val lowerCaseInput = input.lowercase()
        val entry = searchHistoryDao.getEntry(lowerCaseInput)?.let {
            it.copy(
                count = it.count + 1,
                date = System.currentTimeMillis()
            )
        } ?: SearchHistory(
            id = 0L,
            input = lowerCaseInput,
            date = System.currentTimeMillis()
        )
        searchHistoryDao.upsert(entry)
    }

    override suspend fun deleteFromHistory(input: String) =
        searchHistoryDao.delete(input.lowercase())
}