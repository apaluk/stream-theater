package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CastDto(
    @Json(name = "name")
    val name: String,
    @Json(name = "order")
    val order: Int?,
    @Json(name = "role")
    val role: String?,
    @Json(name = "thumbnail")
    val thumbnail: String?
)