package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TotalDto(
    @Json(name = "relation")
    val relation: String,
    @Json(name = "value")
    val value: Int
)