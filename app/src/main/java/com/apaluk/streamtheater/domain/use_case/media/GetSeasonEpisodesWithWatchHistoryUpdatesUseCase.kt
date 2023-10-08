package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.convertNonSuccess
import com.apaluk.streamtheater.domain.model.media.TvShowSeasonEpisodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSeasonEpisodesWithWatchHistoryUpdatesUseCase @Inject constructor(
    private val getSeasonEpisodes: GetSeasonEpisodesUseCase,
    private val getSelectedEpisode: GetSelectedEpisodeUseCase,
    private val getEpisodesWithWatchHistoryUpdates: GetEpisodesWithWatchHistoryUpdatesUseCase,
    private val resourcesManager: ResourcesManager
) {
    operator fun invoke(mediaId: String, seasonId: String): Flow<Resource<TvShowSeasonEpisodes>> = flow {
        emit(Resource.Loading())
        val episodes = getSeasonEpisodes(seasonId)
        if(episodes !is Resource.Success) {
            emit(episodes.convertNonSuccess())
            return@flow
        } else if(episodes.data == null) {
            emit(Resource.Error(resourcesManager.getString(R.string.st_media_error_no_episodes)))
        } else {
            getEpisodesWithWatchHistoryUpdates(mediaId, seasonId, episodes.data)
                .map {
                    TvShowSeasonEpisodes(
                        episodes = it,
                        selectedEpisodeIndex = getSelectedEpisode(mediaId, seasonId, it)
                    )
                }.collect {
                    emit(Resource.Success(it))
                }
        }
    }
}
