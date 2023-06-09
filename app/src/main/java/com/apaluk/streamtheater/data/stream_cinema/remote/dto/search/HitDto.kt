package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HitDto(
    @Json(name = "_id")
    val id: String,
    @Json(name = "_ignored")
    val ignored: List<String>?,
    @Json(name = "_index")
    val index: String,
    @Json(name = "_score")
    val score: Double,
    @Json(name = "_source")
    val source: SourceDto
)