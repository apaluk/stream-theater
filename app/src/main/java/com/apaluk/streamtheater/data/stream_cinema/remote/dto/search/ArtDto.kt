package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtDto(
    @Json(name = "fanart")
    val fanart: String,
    @Json(name = "poster")
    val poster: String
)