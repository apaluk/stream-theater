package com.apaluk.streamtheater.data.stream_cinema.remote.dto.media


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AudioDtoX(
    @Json(name = "channels")
    val channels: Int,
    @Json(name = "codec")
    val codec: String,
    @Json(name = "language")
    val language: String?
)