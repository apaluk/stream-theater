package com.apaluk.streamtheater.domain.repository

import com.apaluk.streamtheater.domain.model.search.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getFilteredHistory(filter: String): Flow<List<SearchHistoryItem>>
    suspend fun addToHistory(input: String)
    suspend fun deleteFromHistory(input: String)
}