package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtDtoX(
    @Json(name = "banner")
    val banner: String?,
    @Json(name = "clearart")
    val clearart: String?,
    @Json(name = "clearlogo")
    val clearlogo: String?,
    @Json(name = "fanart")
    val fanart: String?,
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "landscape")
    val landscape: String?,
    @Json(name = "poster")
    val poster: String?,
    @Json(name = "thumb")
    val thumb: String?
)