package com.apaluk.streamtheater.data.stream_cinema.remote.dto.media


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubtitlesDtoX(
    @Json(name = "language")
    val language: String
)