package com.apaluk.streamtheater.ui.media_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.navigation.StNavArgs
import com.apaluk.streamtheater.core.util.mapList
import com.apaluk.streamtheater.core.util.withLeadingZeros
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.StreamsMediaType
import com.apaluk.streamtheater.domain.use_case.media.GetMediaDetailUiStateUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetSeasonEpisodesUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetStreamsUiStateUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnStartStreamUseCase
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.media_detail.tv_show.TvShowPosterData
import com.apaluk.streamtheater.ui.media_detail.util.relativeProgress
import com.apaluk.streamtheater.ui.media_detail.util.tvShowUiState
import com.apaluk.streamtheater.ui.media_detail.util.updateTvShowUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMediaDetailUiState: GetMediaDetailUiStateUseCase,
    getTvShowSeasonEpisodes: GetSeasonEpisodesUseCase,
    private val updateWatchHistoryOnStartStream: UpdateWatchHistoryOnStartStreamUseCase,
    getStreamsUiState: GetStreamsUiStateUseCase
): ViewModel() {

    private val mediaId: String = requireNotNull(savedStateHandle[StNavArgs.MEDIA_ID_ARG])

    private val _uiState = MutableStateFlow(MediaDetailScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val selectedEpisode = combine(
        _uiState.mapNotNull { it.tvShowUiState?.selectedEpisodeIndex }.distinctUntilChanged(),
        _uiState.mapNotNull { it.tvShowUiState?.episodes }.distinctUntilChanged()
    ) { index, episodes ->
        episodes.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val selectedSeason = combine(
        _uiState.mapNotNull { it.tvShowUiState?.selectedSeasonIndex }.distinctUntilChanged(),
        _uiState.mapNotNull { it.tvShowUiState?.seasons }.distinctUntilChanged()
    ) { index, seasons ->
        seasons.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val mediaIdForStreams: StateFlow<String> = selectedEpisode
        .filterNotNull()
        .map { it.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mediaId)

    private val streamsMediaType: StreamsMediaType
    get() = if(selectedEpisode.value != null) StreamsMediaType.TvShowEpisode else StreamsMediaType.Movie

    init {
        // get media detail UI state
        viewModelScope.launch {
            getMediaDetailUiState(mediaId).collect { mediaDetailUiStateResource ->
                _uiState.update {
                    it.copy(
                        uiState = mediaDetailUiStateResource.toUiState(),
                        mediaDetailUiState = mediaDetailUiStateResource.data
                    )
                }
            }
        }
        // get streams
        viewModelScope.launch {
            mediaIdForStreams.collectLatest { mediaId ->
                getStreamsUiState(mediaId, streamsMediaType).collect { streamsUiState ->
                    _uiState.update {
                        it.copy(streamsUiState = streamsUiState)
                    }
                }
            }
        }
        // get episodes on season select
        viewModelScope.launch {
            selectedSeason.filterNotNull().collectLatest { season ->
                getTvShowSeasonEpisodes(mediaId, season.id).collect { seasonEpisodes ->
                    _uiState.updateTvShowUiState {
                        it.copy(
                            episodes = seasonEpisodes.data?.episodes,
                            selectedEpisodeIndex = seasonEpisodes.data?.selectedEpisodeIndex,
                            episodesUiState = seasonEpisodes.toUiState()
                        )
                    }
                }
            }
        }
        // update poster data according to selected episode
        viewModelScope.launch {
            combine(
                selectedSeason,
                selectedEpisode.filterNotNull(),
                uiState.map { it.streamsUiState?.selectedStreamId },
                uiState.map { it.tvShowUiState?.tvShow?.imageUrl}
            ) { season, episode, selectedStream, tvShowImage ->
                val episodeNumber =
                    if(season != null)
                        "S${season.orderNumber.withLeadingZeros(2)}E${episode.orderNumber.withLeadingZeros(2)}"
                    else
                        "E${episode.orderNumber.withLeadingZeros(2)}"
                TvShowPosterData(
                    episodeNumber = episodeNumber,
                    episodeName = episode.title,
                    duration = episode.duration,
                    imageUrl = episode.imageUrl ?: season?.imageUrl ?: tvShowImage,
                    progress = episode.relativeProgress ?: 0f,
                    showPlayButton = selectedStream != null,
                )
            }.collect { posterData ->
                _uiState.updateTvShowUiState { it.copy(posterData = posterData) }
            }
        }
        // fix episodes with no image
        viewModelScope.launch {
            _uiState
                .map { it.mediaDetailUiState}
                .filterIsInstance<TvShowMediaDetailUiState>()
                .mapNotNull { it.episodes }
                .filter { it.any { episode -> episode.imageUrl.isNullOrEmpty() } }
                .mapList { episode ->
                    if(episode.imageUrl == null)
                        episode.copy(imageUrl = _uiState.value.tvShowUiState?.posterData?.imageUrl)
                    else episode
                }
                .filterNot { // don't continue if any imageUrl is still null
                    it.any { episode -> episode.imageUrl.isNullOrEmpty() }
                }
                .collect { episodes ->
                    _uiState.updateTvShowUiState { it.copy(episodes = episodes) }
                }
        }
    }

    fun onMediaDetailAction(action: MediaDetailAction) {
        when(action) {
            MediaDetailAction.PlayDefault -> onPlayDefault()
            is MediaDetailAction.PlayStream -> onPlayStream(action)
            is MediaDetailAction.SelectTvShowSeason -> onSelectTvShowSeason(action)
            is MediaDetailAction.SelectTvShowEpisode -> onSelectTvShowEpisode(action)
        }
    }

    private fun onPlayDefault() {
        _uiState.value.streamsUiState?.selectedStreamId?.let { streamIdent ->
            onPlayStream(MediaDetailAction.PlayStream(MediaStream(ident = streamIdent)))
        }
    }

    private fun onPlayStream(action: MediaDetailAction.PlayStream) {
        viewModelScope.launch {
            val watchHistoryId = updateWatchHistoryOnStartStream(
                stream = action.stream,
                mediaId = mediaId,
                season = selectedSeason.value?.id,
                episode = selectedEpisode.value?.id
            )
            _uiState.value.playStreamEvent.emit(
                PlayStreamParams(
                    ident = action.stream.ident,
                    watchHistoryId = watchHistoryId
                )
            )
        }
    }

    private fun onSelectTvShowSeason(action: MediaDetailAction.SelectTvShowSeason) {
        _uiState.updateTvShowUiState { it.copy(selectedSeasonIndex = action.seasonIndex) }
    }

    private fun onSelectTvShowEpisode(action: MediaDetailAction.SelectTvShowEpisode) {
        if(action.episodeIndex != _uiState.value.tvShowUiState?.selectedEpisodeIndex) {
            _uiState.update { it.copy(streamsUiState = null) }
            _uiState.updateTvShowUiState { it.copy(selectedEpisodeIndex = action.episodeIndex) }
        }
    }
}
