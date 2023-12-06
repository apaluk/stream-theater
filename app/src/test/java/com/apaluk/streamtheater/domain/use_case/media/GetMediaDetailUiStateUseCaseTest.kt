package com.apaluk.streamtheater.domain.use_case.media

import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.mockkResourcesManager
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.MediaProgress
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.model.media.WatchHistoryEntry
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.ui.media.media_detail.MovieMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.TvShowMediaDetailUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetMediaDetailUiStateUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getMediaDetailUiStateUseCase: GetMediaDetailUiStateUseCase

    @MockK
    private lateinit var getMediaDetailsUseCase: GetMediaDetailsUseCase

    @MockK
    private lateinit var streamCinemaRepository: StreamCinemaRepository

    @MockK
    private lateinit var watchHistoryRepository: WatchHistoryRepository

    @MockK
    private lateinit var getSelectedSeasonUseCase: GetSelectedSeasonUseCase

    @MockK
    private lateinit var getSelectedEpisodeUseCase: GetSelectedEpisodeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when media detail is error, emit error`() = runTest {
        coEvery { getMediaDetailsUseCase.invoke(any()) } returns Resource.Error()
        createUseCase()

        val result = getMediaDetailUiStateUseCase("mediaId").last()
        assertThat(result).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun `when media is movie, emit movie with updated progress`() = runTest {
        val movie = MediaDetailMovie(
            id = "id0",
            title = "",
            originalTitle = null,
            imageUrl = null,
            year = null,
            directors = emptyList(),
            writer = emptyList(),
            cast = emptyList(),
            genre = emptyList(),
            plot = null,
            duration = 0,
        )
        val watchHistoryEntry = WatchHistoryEntry(
            id = 0,
            mediaId = "",
            seasonId = null,
            episodeId = null,
            streamId = 0,
            progressSeconds = 100,
            lastUpdate = 0,
            isWatched = false
        )
        coEvery { getMediaDetailsUseCase.invoke(any()) } returns Resource.Success(movie)
        every { watchHistoryRepository.getMediaWatchHistory(any()) } returns flowOf(listOf(watchHistoryEntry))
        createUseCase()

        getMediaDetailUiStateUseCase("").test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(Resource.Success::class.java)
            assertThat((result as Resource.Success).data).isInstanceOf(MovieMediaDetailUiState::class.java)
            assertThat((result.data as MovieMediaDetailUiState).movie).isEqualTo(movie)
            val updatedResult = awaitItem()
            assertThat(updatedResult).isInstanceOf(Resource.Success::class.java)
            assertThat((updatedResult as Resource.Success).data).isInstanceOf(MovieMediaDetailUiState::class.java)
            assertThat((updatedResult.data as MovieMediaDetailUiState).movie.progress).isEqualTo(MediaProgress(100, false))
            awaitComplete()
        }
    }

    @Test
    fun `when media is tv show, emit tv show with seasons`() = runTest {
        val tvShow = MediaDetailTvShow(
            id = "tvShowId",
            title = "",
            originalTitle = null,
            imageUrl = null,
            years = null,
            genre = emptyList(),
            plot = null,
            cast = emptyList(),
            infoText = null,
            numSeasons = 0,
            duration = 0,
        )
        val seasons = (1..3).map(::createSeason)
        val episodes = (1..5).map(::createEpisode)
        coEvery { getMediaDetailsUseCase.invoke(any()) } returns Resource.Success(tvShow)
        every { streamCinemaRepository.getTvShowChildren(any()) } returns
                flowOf(Resource.Success(seasons)) andThen flowOf(Resource.Success(episodes))
        coEvery { getSelectedSeasonUseCase.invoke(any(), any()) } returns 1
        coEvery { getSelectedEpisodeUseCase.invoke(any(), any(), any()) } returns 4
        createUseCase()

        getMediaDetailUiStateUseCase("").test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(Resource.Success::class.java)
            assertThat((result as Resource.Success).data).isInstanceOf(TvShowMediaDetailUiState::class.java)
            assertThat((result.data as TvShowMediaDetailUiState).tvShow.id).isEqualTo("tvShowId")
            val resultWithSeasons = (awaitItem() as Resource.Success).data as TvShowMediaDetailUiState
            assertThat(resultWithSeasons.seasons?.size).isEqualTo(3)
            awaitComplete()
        }
    }

    @Test
    fun `when media is miniseries, emit tv show with episodes and without seasons`() = runTest {
        val tvShow = MediaDetailTvShow(
            id = "tvShowId",
            title = "",
            originalTitle = null,
            imageUrl = null,
            years = null,
            genre = emptyList(),
            plot = null,
            cast = emptyList(),
            infoText = null,
            numSeasons = 0,
            duration = 0,
        )
        val episodes = (1..5).map(::createEpisode)
        coEvery { getMediaDetailsUseCase.invoke(any()) } returns Resource.Success(tvShow)
        every { streamCinemaRepository.getTvShowChildren(any()) } returns flowOf(Resource.Success(episodes))
        coEvery { getSelectedEpisodeUseCase.invoke(any(), any(), any()) } returns 4
        createUseCase()

        getMediaDetailUiStateUseCase("").test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val result = awaitItem()
            assertThat(result).isInstanceOf(Resource.Success::class.java)
            assertThat((result as Resource.Success).data).isInstanceOf(TvShowMediaDetailUiState::class.java)
            assertThat((result.data as TvShowMediaDetailUiState).tvShow.id).isEqualTo("tvShowId")
            val resultWithEpisodes = (awaitItem() as Resource.Success).data as TvShowMediaDetailUiState
            assertThat(resultWithEpisodes.seasons).isNull()
            assertThat(resultWithEpisodes.episodes).isNotEmpty()
            awaitComplete()
        }
    }

    private fun createUseCase() {
        getMediaDetailUiStateUseCase = GetMediaDetailUiStateUseCase(
            getMediaDetailsUseCase,
            streamCinemaRepository,
            watchHistoryRepository,
            getSelectedSeasonUseCase,
            getSelectedEpisodeUseCase,
            mockkResourcesManager()
        )
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