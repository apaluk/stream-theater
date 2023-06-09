package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponseDto(
    @Json(name = "hits")
    val hits: HitsDto,
    @Json(name = "_shards")
    val shards: ShardsDto,
    @Json(name = "timed_out")
    val timedOut: Boolean,
    @Json(name = "took")
    val took: Int
)