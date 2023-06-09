package com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaStreamsResponseItemDto(
    @Json(name = "audio")
    val audio: List<AudioDto>,
    @Json(name = "date_added")
    val dateAdded: String,
    @Json(name = "_id")
    val id: String,
    @Json(name = "ident")
    val ident: String,
    @Json(name = "media")
    val media: String,
    @Json(name = "name")
    val name: String?,
    @Json(name = "provider")
    val provider: String,
    @Json(name = "size")
    val size: Long,
    @Json(name = "subtitles")
    val subtitles: List<SubtitleDto>,
    @Json(name = "validated")
    val validated: Boolean?,
    @Json(name = "video")
    val video: List<VideoDto>
)