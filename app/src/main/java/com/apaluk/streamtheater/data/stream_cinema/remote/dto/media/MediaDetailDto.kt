package com.apaluk.streamtheater.data.stream_cinema.remote.dto.media


import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.I18nInfoLabelDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaDetailDto(
    @Json(name = "adult")
    val adult: Boolean?,
    @Json(name = "available_streams")
    val availableStreams: AvailableStreamsDto,
    @Json(name = "budget")
    val budget: Int?,
    @Json(name = "cast")
    val cast: List<CastDto>,
    @Json(name = "children_count")
    val childrenCount: Int,
    @Json(name = "country")
    val country: String?,
    @Json(name = "i18n_info_labels")
    val i18nInfoLabels: List<I18nInfoLabelDto>,
    @Json(name = "_id")
    val id: String,
    @Json(name = "info_labels")
    val infoLabels: InfoLabelsDto,
    @Json(name = "is_concert")
    val isConcert: Boolean?,
    @Json(name = "play_count")
    val playCount: Int?,
    @Json(name = "ratings")
    val ratings: RatingsDto,
    @Json(name = "revenue")
    val revenue: Long?,
    @Json(name = "services")
    val services: ServicesDto,
    @Json(name = "stream_info")
    val streamInfo: StreamInfoDto,
    @Json(name = "videos")
    val videos: List<VideoDtoX>
)