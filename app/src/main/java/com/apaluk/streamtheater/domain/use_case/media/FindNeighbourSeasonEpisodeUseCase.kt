package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.isNullOrEmptyList
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.model.media.FindNeighbourSeasonEpisodeResult
import com.apaluk.streamtheater.domain.model.media.util.tryGetEpisodes
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import kotlinx.coroutines.flow.last
import timber.log.Timber
import javax.inject.Inject

class FindNeighbourSeasonEpisodeUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository
) {
    enum class NeighbourType {
        Previous, Next
    }

    suspend operator fun invoke(
        seasons: List<TvShowSeason>,
        currentSeasonIndex: Int,
        currentSeasonEpisodes: List<TvShowEpisode>,
        currentEpisodeIndex: Int,
        neighbourType: NeighbourType
    ): FindNeighbourSeasonEpisodeResult? {
        val neighbourEpisodeIndex = currentEpisodeIndex.neighbourIndex(neighbourType)
        if(neighbourEpisodeIndex in currentSeasonEpisodes.indices) {
            return FindNeighbourSeasonEpisodeResult(
                seasonIndex = currentSeasonIndex,
                seasonId = seasons[currentSeasonIndex].id,
                episodeIndex = neighbourEpisodeIndex,
                episodeId = currentSeasonEpisodes[neighbourEpisodeIndex].id,
                seasonHasChanged = false
            )
        }
        else {
            // neighbour episode is in another season
            val neighbourSeasonIndex = currentSeasonIndex.neighbourIndex(neighbourType)
                .takeIf { it in seasons.indices } ?: return null
            val neighbourSeason = seasons[neighbourSeasonIndex]

            // try get neighbour season episodes
            val episodes = streamCinemaRepository.getTvShowChildren(neighbourSeason.id).last().tryGetEpisodes()
                .takeIf { it.data.isNullOrEmptyList().not() }?.data ?: return null

            // find selected episode
            val episodeIndex = when(neighbourType) {
                NeighbourType.Previous -> episodes.lastIndex
                NeighbourType.Next -> 0
            }

            val episodesText = episodes.map { "${it.title} (${it.id})" }.joinToString(", ")
            Timber.d("xxx Found neighbour season episodes selecting index:$episodeIndex from: $episodesText")

            return FindNeighbourSeasonEpisodeResult(
                seasonIndex = neighbourSeasonIndex,
                seasonId = neighbourSeason.id,
                episodeIndex = episodeIndex,
                episodeId = episodes[episodeIndex].id,
                seasonHasChanged = true
            )
        }
    }

    private fun Int.neighbourIndex(neighbourType: NeighbourType) = when(neighbourType) {
        NeighbourType.Previous -> this - 1
        NeighbourType.Next -> this + 1
    }

}