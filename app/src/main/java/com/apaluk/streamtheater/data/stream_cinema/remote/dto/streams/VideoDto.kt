package com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoDto(
    @Json(name = "aspect")
    val aspect: Double,
    @Json(name = "codec")
    val codec: String,
    @Json(name = "3d")
    val d: Boolean,
    @Json(name = "duration")
    val duration: Double,
    @Json(name = "height")
    val height: Int,
    @Json(name = "_id")
    val id: String?,
    @Json(name = "width")
    val width: Int
)