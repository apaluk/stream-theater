package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoDtoX(
    @Json(name = "lang")
    val lang: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "size")
    val size: Int?,
    @Json(name = "subtitles")
    val subtitles: List<SubtitleDto?>?,
    @Json(name = "type")
    val type: String,
    @Json(name = "url")
    val url: String
)