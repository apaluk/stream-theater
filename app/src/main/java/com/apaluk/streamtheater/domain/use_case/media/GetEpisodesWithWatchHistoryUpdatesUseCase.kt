package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.util.toMediaProgress
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetEpisodesWithWatchHistoryUpdatesUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
    operator fun invoke(mediaId: String, seasonId: String?, episodes: List<TvShowEpisode>): Flow<List<TvShowEpisode>> =
        watchHistoryRepository.getEpisodesWatchHistory(mediaId, seasonId)
            .map { history ->
                episodes.map { episode ->
                    val progress = history
                        .find { it.episodeId == episode.id }
                        ?.toMediaProgress()
                    episode.copy(progress = progress)
                }
            }
}