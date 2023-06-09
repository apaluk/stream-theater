package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastChildrenDateAddedDto(
    @Json(name = "date_added")
    val dateAdded: String,
    @Json(name = "lang")
    val lang: String
)