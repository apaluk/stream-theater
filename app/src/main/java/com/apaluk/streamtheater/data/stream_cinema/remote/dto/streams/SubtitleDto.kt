package com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubtitleDto(
    @Json(name = "forced")
    val forced: Boolean?,
    @Json(name = "_id")
    val id: String?,
    @Json(name = "language")
    val language: String?
)