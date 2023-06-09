package com.apaluk.streamtheater.data.database.dao

import androidx.room.*
import com.apaluk.streamtheater.data.database.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Upsert
    suspend fun upsert(searchHistory: SearchHistory)

    @Query("SELECT * FROM searchHistory WHERE input=:entry")
    suspend fun getEntry(entry: String): SearchHistory?

    @Query("SELECT * FROM searchHistory WHERE input LIKE '%' || :filter || '%' ORDER BY date DESC")
    fun filterSearchHistory(filter: String): Flow<List<SearchHistory>>

    @Query("DELETE FROM searchHistory WHERE input=:entry")
    suspend fun delete(entry: String)
}