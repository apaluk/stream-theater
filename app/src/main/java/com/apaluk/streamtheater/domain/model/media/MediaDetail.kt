package com.apaluk.streamtheater.domain.model.media

sealed class MediaDetail

data class MediaDetailMovie(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val imageUrl: String?,
    val year: String?,
    val directors: List<String>,
    val writer: List<String>,
    val cast: List<String>,
    val genre: List<String>,
    val plot: String?,
    val duration: Int,
    val progress: MediaProgress? = null
): MediaDetail()

data class MediaDetailTvShow(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val imageUrl: String?,
    val years: String?,
    val genre: List<String>,
    val plot: String?,
    val cast: List<String>,
    val numSeasons: Int,
    val duration: Int,
    val progress: MediaProgress? = null
): MediaDetail()

sealed class TvShowChild

data class TvShowSeason(
    val id: String,
    val orderNumber: Int,
    val title: String?,
    val year: String?,
    val directors: List<String>,
    val writer: List<String>,
    val cast: List<String>,
    val genre: List<String>,
    val plot: String?,
    val imageUrl: String?,
): TvShowChild()

data class TvShowEpisode(
    val id: String,
    val orderNumber: Int,
    val title: String?,
    val year: String?,
    val directors: List<String>,
    val writer: List<String>,
    val cast: List<String>,
    val genre: List<String>,
    val plot: String?,
    val imageUrl: String?,
    val thumbImageUrl: String?,
    val duration: Int,
    val progress: MediaProgress? = null
): TvShowChild()

data class MediaProgress(
    val progressSeconds: Int,
    val isWatched: Boolean
)

data class TvShowSeasonEpisodes(
    val episodes: List<TvShowEpisode> = emptyList(),
    val selectedEpisodeIndex: Int? = null
)