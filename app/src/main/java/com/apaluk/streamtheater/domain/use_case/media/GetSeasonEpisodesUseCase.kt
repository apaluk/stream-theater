package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.util.tryGetEpisodes
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetSeasonEpisodesUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository
) {

    suspend operator fun invoke(seasonId: String): Resource<List<TvShowEpisode>?> =
        streamCinemaRepository.getTvShowChildren(seasonId).last().tryGetEpisodes()
}