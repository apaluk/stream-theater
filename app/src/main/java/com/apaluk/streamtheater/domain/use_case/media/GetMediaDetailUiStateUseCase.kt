package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.convertNonSuccess
import com.apaluk.streamtheater.domain.model.media.util.toMediaProgress
import com.apaluk.streamtheater.domain.model.media.util.tryGetEpisodes
import com.apaluk.streamtheater.domain.model.media.util.tryGetSeasons
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.domain.use_case.media.util.generalInfoText
import com.apaluk.streamtheater.domain.use_case.media.util.getYears
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.media.media_detail.MediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.MovieMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.TvShowMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.util.toMediaDetailUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMediaDetailUiStateUseCase @Inject constructor(
    private val getMediaDetailsUseCase: GetMediaDetailsUseCase,
    private val streamCinemaRepository: StreamCinemaRepository,
    private val watchHistoryRepository: WatchHistoryRepository,
    private val getSelectedSeasonUseCase: GetSelectedSeasonUseCase,
    private val getSelectedEpisodeUseCase: GetSelectedEpisodeUseCase,
    private val resourcesManager: ResourcesManager
) {
    operator fun invoke(mediaId: String): Flow<Resource<MediaDetailUiState>> = flow {
        emit(Resource.Loading())
        val mediaDetailResource = getMediaDetailsUseCase(mediaId)
        if (mediaDetailResource !is Resource.Success) {
            emit(mediaDetailResource.convertNonSuccess())
        } else if (mediaDetailResource.data == null) {
            emit(Resource.Error())
        } else {
            val mediaDetailUiState = mediaDetailResource.data.toMediaDetailUiState(resourcesManager)
            emit(Resource.Success(mediaDetailUiState))
            when(mediaDetailUiState) {
                is MovieMediaDetailUiState -> {
                    // collect movie progress and keep updating it
                    emitAll(watchHistoryRepository.getMediaWatchHistory(mediaId)
                        .map {
                            Resource.Success(
                                mediaDetailUiState.copy(
                                    movie = mediaDetailUiState.movie.copy(
                                        progress = it.firstOrNull()?.toMediaProgress()
                                    )
                                )
                            )
                        }
                    )
                }
                is TvShowMediaDetailUiState -> {
                    // get TV show children (seasons or episodes, we don't know yet)
                    val children = streamCinemaRepository.getTvShowChildren(mediaId).last()
                    if(children !is Resource.Success) {
                        emit(children.convertNonSuccess())
                    } else if(children.data == null) {
                        emit(Resource.Error())
                    } else {
                        // try to get seasons. If we get null, it's probably miniseries.
                        val seasons = children.tryGetSeasons()
                        if(seasons !is Resource.Success) {
                            emit(seasons.convertNonSuccess())
                        } else {
                            if(seasons.data != null) {
                                // we found seasons. Get selected season and update UI state
                                val selectedSeasonIndex = getSelectedSeasonUseCase(mediaId, seasons.data)
                                val years = seasons.data.getYears()
                                val tvShowWithYears = mediaDetailUiState.tvShow.copy(
                                    years = years,
                                    infoText = mediaDetailUiState.tvShow.generalInfoText(resourcesManager, years)
                                )
                                emit(Resource.Success(
                                    mediaDetailUiState.copy(
                                        tvShow = tvShowWithYears,
                                        seasons = seasons.data,
                                        selectedSeasonIndex = selectedSeasonIndex,
                                        episodesUiState = UiState.Loading,
                                        episodes = null,
                                        selectedEpisodeIndex = null
                                    )
                                ))
                                // season episodes will be updated in ViewModel, since season selection
                                // can be changed by user any time
                            } else {
                                // no seasons found, should be miniseries
                                val episodes = children.tryGetEpisodes()
                                if(episodes !is Resource.Success) {
                                    emit(Resource.Success(mediaDetailUiState.copy(episodesUiState = episodes.toUiState())))
                                } else if(episodes.data == null) {
                                    emit(Resource.Error(resourcesManager.getString(R.string.st_media_error_no_episodes)))
                                } else {
                                    // episodes found, update UI state
                                    val selectedEpisodeIndex = getSelectedEpisodeUseCase(mediaId, null, episodes.data)
                                    emit(Resource.Success(
                                        mediaDetailUiState.copy(
                                            episodesUiState = episodes.toUiState(),
                                            episodes = episodes.data,
                                            selectedEpisodeIndex = selectedEpisodeIndex
                                        )
                                    ))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}