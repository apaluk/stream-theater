package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.isNullOrEmptyList
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.model.media.FindNeighbourSeasonEpisodeResult
import com.apaluk.streamtheater.domain.model.media.SeasonEpisodeNeighbourType
import com.apaluk.streamtheater.domain.model.media.util.tryGetEpisodes
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class FindNeighbourSeasonEpisodeUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository
) {
    suspend operator fun invoke(
        seasons: List<TvShowSeason>?,
        currentSeasonIndex: Int?,
        currentEpisodes: List<TvShowEpisode>,
        currentEpisodeIndex: Int,
        seasonEpisodeNeighbourType: SeasonEpisodeNeighbourType
    ): FindNeighbourSeasonEpisodeResult? {
        val neighbourEpisodeIndex = currentEpisodeIndex.neighbourIndex(seasonEpisodeNeighbourType)
        val seasonId = if (currentSeasonIndex != null) seasons?.get(currentSeasonIndex)?.id else null
        if(neighbourEpisodeIndex in currentEpisodes.indices) {
            return FindNeighbourSeasonEpisodeResult(
                seasonIndex = currentSeasonIndex,
                seasonId = seasonId,
                episodeIndex = neighbourEpisodeIndex,
                episodeId = currentEpisodes[neighbourEpisodeIndex].id,
                seasonHasChanged = false
            )
        }
        else {
            // neighbour episode is in another season
            // if there is no neighbour season available, we are probably in miniseries, so return null
            currentSeasonIndex ?: return null
            seasons ?: return null

            val neighbourSeasonIndex = currentSeasonIndex.neighbourIndex(seasonEpisodeNeighbourType)
                .takeIf { it in seasons.indices } ?: return null
            val neighbourSeason = seasons[neighbourSeasonIndex]

            // try get neighbour season episodes
            val episodes = streamCinemaRepository.getTvShowChildren(neighbourSeason.id).last().tryGetEpisodes()
                .takeIf { it.data.isNullOrEmptyList().not() }?.data ?: return null

            // find selected episode
            val episodeIndex = when(seasonEpisodeNeighbourType) {
                SeasonEpisodeNeighbourType.Previous -> episodes.lastIndex
                SeasonEpisodeNeighbourType.Next -> 0
            }

            return FindNeighbourSeasonEpisodeResult(
                seasonIndex = neighbourSeasonIndex,
                seasonId = neighbourSeason.id,
                episodeIndex = episodeIndex,
                episodeId = episodes[episodeIndex].id,
                seasonHasChanged = true
            )
        }
    }

    private fun Int.neighbourIndex(seasonEpisodeNeighbourType: SeasonEpisodeNeighbourType) = when(seasonEpisodeNeighbourType) {
        SeasonEpisodeNeighbourType.Previous -> this - 1
        SeasonEpisodeNeighbourType.Next -> this + 1
    }

}