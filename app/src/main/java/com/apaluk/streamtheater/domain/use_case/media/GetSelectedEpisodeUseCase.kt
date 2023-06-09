package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSelectedEpisodeUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
    suspend operator fun invoke(mediaId: String, seasonId: String?, episodes: List<TvShowEpisode>): Int? {
        val episodesHistory = watchHistoryRepository.getEpisodesWatchHistory(mediaId, seasonId).first()
        val lastWatchedEpisode = episodesHistory.firstOrNull { it.episodeId != null } ?: return 0
        val lastWatchedEpisodeIndex = episodes.indexOfFirst { it.id == lastWatchedEpisode.episodeId }
        val newIndex = if (lastWatchedEpisode.isWatched) lastWatchedEpisodeIndex + 1 else lastWatchedEpisodeIndex
        if(newIndex in episodes.indices)
            return newIndex

        // find first not watched episode in this season
        episodes.forEachIndexed { index, tvShowEpisode ->
            if(!episodesHistory.any { it.episodeId == tvShowEpisode.id })
                return index
        }
        // all episodes watched
        return null
    }
}