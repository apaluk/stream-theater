package com.apaluk.streamtheater.domain.model.search

import java.util.*

data class SearchHistoryItem(
    val text: String,
    val lastSearchDate: Date
)