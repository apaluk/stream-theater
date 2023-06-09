package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class I18nInfoLabelDto(
    @Json(name = "art")
    val art: ArtDtoX?,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "name")
    val name: String?,
    @Json(name = "parent_titles")
    val parentTitles: List<Any>?,
    @Json(name = "plot")
    val plot: String?,
    @Json(name = "plotoutline")
    val plotoutline: Any?,
    @Json(name = "rating")
    val rating: Double?,
    @Json(name = "tagline")
    val tagline: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "trailer")
    val trailer: String?,
    @Json(name = "votes")
    val votes: String?
)