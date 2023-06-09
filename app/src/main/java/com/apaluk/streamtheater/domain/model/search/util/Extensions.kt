package com.apaluk.streamtheater.domain.model.search.util

import com.apaluk.streamtheater.data.database.model.SearchHistory
import com.apaluk.streamtheater.domain.model.search.SearchHistoryItem
import java.util.*

fun SearchHistory.toSearchHistoryItem(): SearchHistoryItem = SearchHistoryItem(
    text = input,
    lastSearchDate = Date(date)
)