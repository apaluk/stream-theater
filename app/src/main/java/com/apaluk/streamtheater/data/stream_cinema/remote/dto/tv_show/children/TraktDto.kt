package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TraktDto(
    @Json(name = "rating")
    val rating: Double,
    @Json(name = "votes")
    val votes: Int
)