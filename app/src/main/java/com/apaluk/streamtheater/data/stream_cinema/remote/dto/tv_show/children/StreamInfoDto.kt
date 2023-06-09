package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StreamInfoDto(
    @Json(name = "audio")
    val audio: AudioDtoX?,
    @Json(name = "video")
    val video: VideoDto?
)