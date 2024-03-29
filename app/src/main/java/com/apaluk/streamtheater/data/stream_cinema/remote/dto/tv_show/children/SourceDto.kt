package com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SourceDto(
    @Json(name = "available_streams")
    val availableStreams: AvailableStreamsDto?,
    @Json(name = "cast")
    val cast: List<CastDto>,
    @Json(name = "children_count")
    val childrenCount: Int,
    @Json(name = "collections")
    val collections: List<Any>?,
    @Json(name = "i18n_info_labels")
    val i18nInfoLabels: List<com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.I18nInfoLabelDto>,
    @Json(name = "info_labels")
    val infoLabels: InfoLabelsDto,
    @Json(name = "is_concert")
    val isConcert: Boolean?,
    @Json(name = "languages")
    val languages: List<Any>,
    @Json(name = "last_children_date_added")
    val lastChildrenDateAdded: List<LastChildrenDateAddedDto>?,
    @Json(name = "networks")
    val networks: List<String>?,
    @Json(name = "parent_id")
    val parentId: String,
    @Json(name = "parent_info_labels")
    val parentInfoLabels: ParentInfoLabelsDto,
    @Json(name = "play_count")
    val playCount: Int,
    @Json(name = "popularity")
    val popularity: Double,
    @Json(name = "premieres")
    val premieres: List<PremiereDto>?,
    @Json(name = "ratings")
    val ratings: RatingsDto?,
    @Json(name = "root_parent")
    val rootParent: String,
    @Json(name = "services")
    val services: ServicesDto,
    @Json(name = "stream_info")
    val streamInfo: StreamInfoDto?,
    @Json(name = "tags")
    val tags: List<Any>,
    @Json(name = "total_children_count")
    val totalChildrenCount: Int?,
    @Json(name = "trending")
    val trending: Double,
    @Json(name = "videos")
    val videos: List<VideoDto>
)