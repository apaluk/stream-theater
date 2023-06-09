package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParentInfoLabelsDto(
    @Json(name = "originaltitle")
    val originaltitle: List<String>
)