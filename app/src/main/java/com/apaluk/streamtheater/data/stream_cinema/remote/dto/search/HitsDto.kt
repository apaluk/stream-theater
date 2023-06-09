package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HitsDto(
    @Json(name = "hits")
    val hits: List<HitDto>,
    @Json(name = "max_score")
    val maxScore: Double?,
    @Json(name = "total")
    val total: TotalDto
)