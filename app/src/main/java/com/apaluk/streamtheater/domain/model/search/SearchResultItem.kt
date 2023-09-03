package com.apaluk.streamtheater.domain.model.search

data class SearchResultItem(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val generalInfo: String,
    val cast: String,
    val imageUrl: String?
)
