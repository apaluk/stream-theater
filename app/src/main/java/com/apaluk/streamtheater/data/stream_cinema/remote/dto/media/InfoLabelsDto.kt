package com.apaluk.streamtheater.data.stream_cinema.remote.dto.media


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InfoLabelsDto(
    @Json(name = "dateadded")
    val dateadded: String,
    @Json(name = "director")
    val director: List<String>,
    @Json(name = "duration")
    val duration: Int,
    @Json(name = "genre")
    val genre: List<String>,
    @Json(name = "mediatype")
    val mediatype: MediaTypeDto,
    @Json(name = "mpaa")
    val mpaa: String?,
    @Json(name = "originaltitle")
    val originaltitle: String?,
    @Json(name = "premiered")
    val premiered: String?,
    @Json(name = "studio")
    val studio: List<String>?,
    @Json(name = "writer")
    val writer: List<String>,
    @Json(name = "year")
    val year: Int?,
    @Json(name = "season")
    val season: Int?,
    @Json(name = "episode")
    val episode: Int?
)