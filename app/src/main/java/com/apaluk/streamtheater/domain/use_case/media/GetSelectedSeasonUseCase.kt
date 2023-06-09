package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSelectedSeasonUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {

    suspend operator fun invoke(mediaId: String, seasons: List<TvShowSeason>): Int {
        val latestSeasonWatched = watchHistoryRepository.getMediaWatchHistory(mediaId).first()
            .firstOrNull { it.seasonId != null }
            ?.seasonId

        latestSeasonWatched?.let { seasonId ->
            val seasonIndex = seasons.indexOfFirst { it.id == seasonId }
            if(seasonIndex >= 0)
                return seasonIndex
        }

        // find 1st regular season
        val firstSeason = seasons.indexOfFirst { it.orderNumber == 1 }
        return firstSeason.coerceAtLeast(0)
    }
}