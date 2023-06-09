package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatingsDto(
    @Json(name = "trakt")
    val trakt: TraktDto?
)