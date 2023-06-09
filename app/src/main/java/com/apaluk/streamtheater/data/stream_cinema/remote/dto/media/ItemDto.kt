package com.apaluk.streamtheater.data.stream_cinema.remote.dto.media


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemDto(
    @Json(name = "date_added")
    val dateAdded: String,
    @Json(name = "lang")
    val lang: String
)