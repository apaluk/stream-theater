package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShardsDto(
    @Json(name = "failed")
    val failed: Int,
    @Json(name = "skipped")
    val skipped: Int,
    @Json(name = "successful")
    val successful: Int,
    @Json(name = "total")
    val total: Int
)