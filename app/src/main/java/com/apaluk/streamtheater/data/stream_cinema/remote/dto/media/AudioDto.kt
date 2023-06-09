package com.apaluk.streamtheater.data.stream_cinema.remote.dto.media


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AudioDto(
    @Json(name = "items")
    val items: List<ItemDto>,
    @Json(name = "map")
    val map: List<String>
)