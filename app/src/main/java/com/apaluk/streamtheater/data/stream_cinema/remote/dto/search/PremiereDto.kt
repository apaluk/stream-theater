package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PremiereDto(
    @Json(name = "country")
    val country: String?,
    @Json(name = "date")
    val date: String,
    @Json(name = "type")
    val type: String
)