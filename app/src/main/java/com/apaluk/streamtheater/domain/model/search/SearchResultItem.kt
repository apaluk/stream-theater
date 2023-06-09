package com.apaluk.streamtheater.domain.model.search

data class SearchResultItem(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val year: String,
    val genre: List<String>,
    val duration: Int,
    val cast: List<String>,
    val director: List<String>,
    val imageUrl: String?
)
