package com.apaluk.streamtheater.domain.use_case.media

import android.content.Context
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.convertNonSuccess
import com.apaluk.streamtheater.domain.model.media.TvShowSeasonEpisodes
import com.apaluk.streamtheater.domain.model.media.util.tryGetEpisodes
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetSeasonEpisodesUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val getSelectedEpisode: GetSelectedEpisodeUseCase,
    private val getEpisodesWithWatchHistoryUpdates: GetEpisodesWithWatchHistoryUpdatesUseCase,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(mediaId: String, seasonId: String): Flow<Resource<TvShowSeasonEpisodes>> = flow {
        emit(Resource.Loading())
        val episodes = streamCinemaRepository.getTvShowChildren(seasonId).last().tryGetEpisodes()
        if(episodes !is Resource.Success) {
            emit(episodes.convertNonSuccess())
            return@flow
        } else if(episodes.data == null) {
            emit(Resource.Error(context.getString(R.string.st_media_error_no_episodes)))
        } else {
            getEpisodesWithWatchHistoryUpdates(mediaId, seasonId, episodes.data).collect {
                emit(
                    Resource.Success(
                        TvShowSeasonEpisodes(
                            episodes = it,
                            selectedEpisodeIndex = getSelectedEpisode(mediaId, seasonId, it)
                        )
                    )
                )
            }
        }
    }
}
