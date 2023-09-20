package com.apaluk.streamtheater.ui.media.media_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.navigation.StNavArgs
import com.apaluk.streamtheater.core.util.isNullOrEmptyList
import com.apaluk.streamtheater.core.util.mapList
import com.apaluk.streamtheater.domain.model.media.FindNeighbourSeasonEpisodeResult
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.StreamsMediaType
import com.apaluk.streamtheater.domain.use_case.media.FindNeighbourSeasonEpisodeUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetMediaDetailUiStateUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetSeasonEpisodesUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetSeasonEpisodesWithWatchHistoryUpdatesUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetStreamsUiStateUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnStartStreamUseCase
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import com.apaluk.streamtheater.ui.media.media_detail.tv_show.TvShowPosterData
import com.apaluk.streamtheater.ui.media.media_detail.util.relativeProgress
import com.apaluk.streamtheater.ui.media.media_detail.util.seasonEpisodeText
import com.apaluk.streamtheater.ui.media.media_detail.util.toPlayerMediaInfo
import com.apaluk.streamtheater.ui.media.media_detail.util.tvShowUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMediaDetailUiState: GetMediaDetailUiStateUseCase,
    getTvShowSeasonEpisodes: GetSeasonEpisodesWithWatchHistoryUpdatesUseCase,
    private val updateWatchHistoryOnStartStream: UpdateWatchHistoryOnStartStreamUseCase,
    getStreamsUiState: GetStreamsUiStateUseCase,
    private val findNeighbourSeasonEpisodeUseCase: FindNeighbourSeasonEpisodeUseCase,
    private val getSeasonEpisodesUseCase: GetSeasonEpisodesUseCase
): BaseViewModel<MediaDetailScreenUiState, MediaDetailEvent, MediaDetailAction>() {

    private val mediaId: String = requireNotNull(savedStateHandle[StNavArgs.MEDIA_ID_ARG])

    override val initialState: MediaDetailScreenUiState = MediaDetailScreenUiState()

    private val isJumpingToNeighbourEpisode = MutableStateFlow(false)

    private val selectedEpisode = combine(
        uiState.mapNotNull { it.tvShowUiState?.selectedEpisodeIndex }.distinctUntilChanged(),
        uiState.mapNotNull { it.tvShowUiState?.episodes }.distinctUntilChanged()
    ) { index, episodes ->
        episodes.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val selectedSeason = combine(
        uiState.mapNotNull { it.tvShowUiState?.selectedSeasonIndex }.distinctUntilChanged(),
        uiState.mapNotNull { it.tvShowUiState?.seasons }.distinctUntilChanged()
    ) { index, seasons ->
        seasons.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val mediaIdForStreams: StateFlow<String> = selectedEpisode
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mediaId)

    private val streamsMediaType: StreamsMediaType
    get() = if(selectedEpisode.value != null) StreamsMediaType.TvShowEpisode else StreamsMediaType.Movie

    init {
        // get media detail UI state
        viewModelScope.launch {
            getMediaDetailUiState(mediaId).collect { mediaDetailUiStateResource ->
                emitUiState {
                    it.copy(
                        uiState = mediaDetailUiStateResource.toUiState(),
                        mediaDetailUiState = mediaDetailUiStateResource.data
                    )
                }
            }
        }
        // get streams
        viewModelScope.launch {
            mediaIdForStreams.collectLatest { childMediaId ->
                getStreamsUiState(childMediaId, mediaId, streamsMediaType).collect { streamsUiState ->
                    emitUiState { it.copy(streamsUiState = streamsUiState) }
                }
            }
        }
        // get episodes on season select
        viewModelScope.launch {
            selectedSeason.filterNotNull().collectLatest { season ->
                getTvShowSeasonEpisodes(mediaId, season.id).collect { seasonEpisodes ->
                    if(isJumpingToNeighbourEpisode.value.not()) {
                        updateTvShowUiState {
                            it.copy(
                                episodes = seasonEpisodes.data?.episodes,
                                selectedEpisodeIndex = seasonEpisodes.data?.selectedEpisodeIndex,
                                episodesUiState = seasonEpisodes.toUiState()
                            )
                        }
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
                TvShowPosterData(
                    episodeNumber = seasonEpisodeText(season, episode),
                    episodeName = episode.title,
                    duration = episode.duration,
                    imageUrl = episode.imageUrl ?: season?.imageUrl ?: tvShowImage,
                    progress = episode.relativeProgress ?: 0f,
                    showPlayButton = selectedStream != null,
                )
            }.collect { posterData ->
                updateTvShowUiState { it.copy(posterData = posterData) }
            }
        }
        // fix episodes with no image
        viewModelScope.launch {
            uiState
                .map { it.mediaDetailUiState}
                .filterIsInstance<TvShowMediaDetailUiState>()
                .mapNotNull { it.episodes }
                .filter { it.any { episode -> episode.imageUrl.isNullOrEmpty() } }
                .mapList { episode ->
                    if(episode.imageUrl == null)
                        episode.copy(imageUrl = uiState.value.tvShowUiState?.posterData?.imageUrl)
                    else episode
                }
                .filterNot { // don't continue if any imageUrl is still null
                    it.any { episode -> episode.imageUrl.isNullOrEmpty() }
                }
                .collect { episodes ->
                    updateTvShowUiState { it.copy(episodes = episodes) }
                }
        }
    }

    override fun handleAction(action: MediaDetailAction) {
        when(action) {
            MediaDetailAction.PlayDefault -> onPlayDefault()
            is MediaDetailAction.PlayStream -> onPlayStream(action)
            is MediaDetailAction.SelectSeasonIndex -> onSelectSeasonIndex(action)
            is MediaDetailAction.SelectEpisodeIndex -> onSelectEpisodeIndex(action)
            MediaDetailAction.SkipToNextVideo -> onSkipToNextVideo()
            MediaDetailAction.SkipToPreviousVideo -> onSkipToPreviousVideo()
        }
    }

    private fun onPlayDefault() {
        uiState.value.streamsUiState?.selectedStreamId?.let { streamIdent ->
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
            emitEvent(
                MediaDetailEvent.PlayStream(
                    PlayStreamParams(
                        ident = action.stream.ident,
                        watchHistoryId = watchHistoryId,
                        mediaInfo = uiState.value.mediaDetailUiState?.toPlayerMediaInfo(),
                        showNextPrevControls = streamsMediaType == StreamsMediaType.TvShowEpisode
                    )
                )
            )
        }
    }

    private fun onSelectSeasonIndex(action: MediaDetailAction.SelectSeasonIndex) {
        updateTvShowUiState { it.copy(selectedSeasonIndex = action.seasonIndex) }
    }

    private fun onSelectEpisodeIndex(action: MediaDetailAction.SelectEpisodeIndex) {
        if(action.episodeIndex != uiState.value.tvShowUiState?.selectedEpisodeIndex) {
            emitUiState { it.copy(streamsUiState = null) }
            updateTvShowUiState { it.copy(selectedEpisodeIndex = action.episodeIndex) }
        }
    }

    private fun onSkipToPreviousVideo() {
        onSkipToNeighbourVideo(FindNeighbourSeasonEpisodeUseCase.NeighbourType.Previous)
    }

    private fun onSkipToNextVideo() {
        onSkipToNeighbourVideo(FindNeighbourSeasonEpisodeUseCase.NeighbourType.Next)
    }

    private fun onSkipToNeighbourVideo(neighbourType: FindNeighbourSeasonEpisodeUseCase.NeighbourType) {
        viewModelScope.launch {
            try {
                isJumpingToNeighbourEpisode.value = true
                emitUiState { it.copy(showSeekingProgressBar = true) }
                val neighbourSeasonEpisode = findNeighbourSeasonEpisode(neighbourType) ?: return@launch
                emitUiState { it.copy(streamsUiState = null) }

                withTimeout(10_000) {
                    if (neighbourSeasonEpisode.seasonId != null && neighbourSeasonEpisode.seasonHasChanged) {
                        updateTvShowUiState {
                            it.copy(selectedSeasonIndex = neighbourSeasonEpisode.seasonIndex,)
                        }
                        // wait until season is selected
                        selectedSeason.first { it?.id == neighbourSeasonEpisode.seasonId }
                        val episodes = getSeasonEpisodesUseCase(neighbourSeasonEpisode.seasonId)
                            .takeIf { it.data.isNullOrEmptyList().not() }?.data ?: return@withTimeout
                        updateTvShowUiState {
                            it.copy(
                                episodes = episodes,
                                selectedEpisodeIndex = neighbourSeasonEpisode.episodeIndex
                            )
                        }
                    }
                    else {
                        updateTvShowUiState {
                            it.copy(selectedEpisodeIndex = neighbourSeasonEpisode.episodeIndex)
                        }
                    }
                    // wait until episode is selected
                    selectedEpisode.first { it?.id == neighbourSeasonEpisode.episodeId }
                    // wait until stream is selected
                    uiState.first { it.streamsUiState?.selectedStreamId != null }
                    onPlayDefault()
                }
                emitUiState { it.copy(showSeekingProgressBar = false) }

                // set isJumpingToNeighbourEpisode to false after 5s delay to prevent watch history
                // updates from setting selected episode
                delay(5_000)
            } catch (e: CancellationException) {
                Timber.w(e)
            }
            finally {
                emitUiState { it.copy(showSeekingProgressBar = false) }
                isJumpingToNeighbourEpisode.value = false
            }
        }
    }

    private fun updateTvShowUiState(newUiStateProducer: (TvShowMediaDetailUiState) -> TvShowMediaDetailUiState) {
        val tvShowUiState = uiState.value.tvShowUiState ?: return
        emitUiState { it.copy(mediaDetailUiState = newUiStateProducer(tvShowUiState)) }
    }

    private suspend fun findNeighbourSeasonEpisode(
        neighbourType: FindNeighbourSeasonEpisodeUseCase.NeighbourType
    ): FindNeighbourSeasonEpisodeResult? {
        return findNeighbourSeasonEpisodeUseCase(
            seasons = uiState.value.tvShowUiState?.seasons,
            currentSeasonIndex = uiState.value.tvShowUiState?.selectedSeasonIndex,
            currentEpisodes = uiState.value.tvShowUiState?.episodes ?: return null,
            currentEpisodeIndex = uiState.value.tvShowUiState?.selectedEpisodeIndex ?: return null,
            neighbourType = neighbourType
        )
    }
}
