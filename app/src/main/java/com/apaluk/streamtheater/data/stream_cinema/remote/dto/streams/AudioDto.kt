package com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AudioDto(
    @Json(name = "channels")
    val channels: Int,
    @Json(name = "codec")
    val codec: String,
    @Json(name = "_id")
    val id: String?,
    @Json(name = "language")
    val language: String?
)