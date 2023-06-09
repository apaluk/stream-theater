package com.apaluk.streamtheater.ui.media_detail

import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.domain.model.media.*
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.media_detail.tv_show.TvShowPosterData

data class MediaDetailScreenUiState(
    val uiState: UiState = UiState.Loading,
    val mediaDetailUiState: MediaDetailUiState? = null,
    val streamsUiState: StreamsUiState? = null,
    val playStreamEvent: SingleEvent<PlayStreamParams> = SingleEvent()
)

sealed class MediaDetailUiState

data class MovieMediaDetailUiState(
    val movie: MediaDetailMovie
): MediaDetailUiState()

data class TvShowMediaDetailUiState(
    val tvShow: MediaDetailTvShow,
    val episodesUiState: UiState = UiState.Idle,
    val selectedSeasonIndex: Int? = null,
    val selectedEpisodeIndex: Int? = null,
    val seasons: List<TvShowSeason>? = null,
    val episodes: List<TvShowEpisode>? = null,
    val posterData: TvShowPosterData? = null
): MediaDetailUiState()

sealed class MediaDetailAction {
    object PlayDefault: MediaDetailAction()
    data class PlayStream(val stream: MediaStream): MediaDetailAction()
    data class SelectTvShowSeason(val seasonIndex: Int): MediaDetailAction()
    data class SelectTvShowEpisode(val episodeIndex: Int): MediaDetailAction()
}

data class StreamsUiState(
    val streams: List<MediaStream>,
    val selectedStreamId: String? = null,
)

data class PlayStreamParams(
    val ident: String,
    val watchHistoryId: Long
)