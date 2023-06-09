package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RottenTomatoesDto(
    @Json(name = "rating")
    val rating: Double?
)