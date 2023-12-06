package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.media.FindNeighbourSeasonEpisodeResult
import com.apaluk.streamtheater.domain.model.media.SeasonEpisodeNeighbourType
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FindNeighbourSeasonEpisodeUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var findNeighbourSeasonEpisodeUseCase: FindNeighbourSeasonEpisodeUseCase

    @MockK
    private lateinit var streamCinemaRepository: StreamCinemaRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when episode is in the middle, find neighbour episode`() = runTest {
        createUseCase()

        val seasons = (1..3).map(::createSeason)
        val episodes = (1..5).map(::createEpisode)
        val result = findNeighbourSeasonEpisodeUseCase.invoke(
            seasons,
            1,
            episodes,
            3,
            SeasonEpisodeNeighbourType.Previous
        )
        assertThat(result).isEqualTo(
            FindNeighbourSeasonEpisodeResult(
                seasonIndex = 1,
                seasonId = "id2",
                episodeIndex = 2,
                episodeId = "id3",
                seasonHasChanged = false
            )
        )
    }

    @Test
    fun `when no previous episode exists, return null`() = runTest {
        createUseCase()

        val seasons = (1..3).map(::createSeason)
        val episodes = (1..5).map(::createEpisode)
        val result = findNeighbourSeasonEpisodeUseCase.invoke(
            seasons,
            0,
            episodes,
            0,
            SeasonEpisodeNeighbourType.Previous
        )
        assertThat(result).isNull()
    }

    @Test
    fun `when no next episode exists, return null`() = runTest {
        createUseCase()

        val seasons = (1..3).map(::createSeason)
        val episodes = (1..5).map(::createEpisode)
        val result = findNeighbourSeasonEpisodeUseCase.invoke(
            seasons,
            2,
            episodes,
            4,
            SeasonEpisodeNeighbourType.Next
        )
        assertThat(result).isNull()
    }

    @Test
    fun `when episode is the first, find last episode in previous season`() = runTest {
        val neighbourEpisodes = (10..13).map(::createEpisode)
        coEvery { streamCinemaRepository.getTvShowChildren(any()) } returns flowOf(
            Resource.Success(neighbourEpisodes)
        )
        createUseCase()

        val seasons = (1..3).map(::createSeason)
        val episodes = (1..5).map(::createEpisode)
        val result = findNeighbourSeasonEpisodeUseCase.invoke(
            seasons,
            1,
            episodes,
            0,
            SeasonEpisodeNeighbourType.Previous
        )
        assertThat(result).isEqualTo(
            FindNeighbourSeasonEpisodeResult(
                seasonIndex = 0,
                seasonId = "id1",
                episodeIndex = 3,
                episodeId = "id13",
                seasonHasChanged = true
            )
        )
    }
    @Test
    fun `when episode is the last, find first episode in next season`() = runTest {
        val neighbourEpisodes = (10..13).map(::createEpisode)
        coEvery { streamCinemaRepository.getTvShowChildren(any()) } returns flowOf(
            Resource.Success(neighbourEpisodes)
        )
        createUseCase()

        val seasons = (1..3).map(::createSeason)
        val episodes = (1..5).map(::createEpisode)
        val result = findNeighbourSeasonEpisodeUseCase.invoke(
            seasons,
            1,
            episodes,
            4,
            SeasonEpisodeNeighbourType.Next
        )
        assertThat(result).isEqualTo(
            FindNeighbourSeasonEpisodeResult(
                seasonIndex = 2,
                seasonId = "id3",
                episodeIndex = 0,
                episodeId = "id10",
                seasonHasChanged = true
            )
        )
    }



    private fun createUseCase() {
        findNeighbourSeasonEpisodeUseCase = FindNeighbourSeasonEpisodeUseCase(streamCinemaRepository)
    }

    private fun createSeason(index: Int) =
        TvShowSeason(
            "id$index",
            index,
            "title$index",
            "2023",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            null,
            null
        )

    private fun createEpisode(index: Int) =
        TvShowEpisode(
            "id$index",
            index,
            "title$index",
            "",
            "2023",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            "",
            null,
            0,
        )
}