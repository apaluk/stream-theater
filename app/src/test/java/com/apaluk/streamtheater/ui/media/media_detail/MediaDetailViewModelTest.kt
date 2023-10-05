package com.apaluk.streamtheater.ui.media.media_detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.apaluk.streamtheater.core.navigation.StNavArgs
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.model.media.TvShowSeasonEpisodes
import com.apaluk.streamtheater.domain.use_case.media.FindNeighbourSeasonEpisodeUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetMediaDetailUiStateUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetSeasonEpisodesUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetSeasonEpisodesWithWatchHistoryUpdatesUseCase
import com.apaluk.streamtheater.domain.use_case.media.GetStreamsUiStateUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnStartStreamUseCase
import com.apaluk.streamtheater.ui.media.media_detail.util.PlayerMediaInfo
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MediaDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MediaDetailViewModel

    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var getMediaDetailUiStateUseCase: GetMediaDetailUiStateUseCase

    @MockK
    private lateinit var getSeasonEpisodesWithWatchHistoryUpdatesUseCase: GetSeasonEpisodesWithWatchHistoryUpdatesUseCase

    @MockK
    private lateinit var updateWatchHistoryOnStartStreamUseCase: UpdateWatchHistoryOnStartStreamUseCase

    @MockK
    private lateinit var getStreamsUiStateUseCase: GetStreamsUiStateUseCase

    @MockK
    private lateinit var findNeighbourSeasonEpisodeUseCase: FindNeighbourSeasonEpisodeUseCase

    @MockK
    private lateinit var getSeasonEpisodesUseCase: GetSeasonEpisodesUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        savedStateHandle = SavedStateHandle(mapOf(StNavArgs.MEDIA_ID_ARG to MEDIA_ID))
    }

    @Test
    fun `given movie media detail, when started, movie media detail ui state is emitted`() = runTest {
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(MovieMediaDetailUiState(movieMediaDetail)))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(StreamsUiState(emptyList(), null))
        createViewModel()

        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.mediaDetailUiState).isEqualTo(MovieMediaDetailUiState(movieMediaDetail))
        }
    }

    @Test
    fun `given tv show media detail, when started, tv show media detail ui state is emitted`() = runTest {
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(TvShowMediaDetailUiState(tvShowMediaDetail)))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(StreamsUiState(emptyList(), null))
        createViewModel()

        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.mediaDetailUiState).isEqualTo(TvShowMediaDetailUiState(tvShowMediaDetail))
        }
    }

    @Test
    fun `given movie media detail, when started, streams ui state is emitted`() = runTest {
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(MovieMediaDetailUiState(movieMediaDetail)))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(
            StreamsUiState(listOf(stream), null)
        )
        createViewModel()

        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.streamsUiState).isEqualTo(StreamsUiState(listOf(stream), null))
        }
    }

    @Test
    fun `given tv show media detail, when started, episodes with watch history are fetched and emitted`() = runTest {
        val tvShowUiState = TvShowMediaDetailUiState(
            tvShow = tvShowMediaDetail,
            seasons = listOf(createSeason(0), createSeason(1)),
            selectedSeasonIndex = 0,
        )
        every { getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke(any(), any()) } returns
                flowOf(Resource.Success(TvShowSeasonEpisodes(tvShowSeasonEpisodes, 1)))
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(tvShowUiState))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(
            StreamsUiState(listOf(stream), null)
        )
        coEvery { findNeighbourSeasonEpisodeUseCase.invoke(any(), any(), any(), any(), any()) } returns null
        createViewModel()

        verify { getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke(any(), any()) }
        viewModel.uiState.test {
            val uiState = awaitItem().mediaDetailUiState as TvShowMediaDetailUiState
            assertThat(uiState.episodes).isEqualTo(tvShowSeasonEpisodes)
            assertThat(uiState.selectedEpisodeIndex).isEqualTo(1)
        }
    }

    @Test
    fun `given tv show media detail, on season select, fetch season episodes`() = runTest {
        val tvShowUiState = TvShowMediaDetailUiState(
            tvShow = tvShowMediaDetail,
            seasons = listOf(createSeason(0), createSeason(1)),
            selectedSeasonIndex = 0,
        )
        every { getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke(any(), any()) } returns
                flowOf(Resource.Success(TvShowSeasonEpisodes(tvShowSeasonEpisodes, 1)))
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(tvShowUiState))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(
            StreamsUiState(listOf(stream), null)
        )
        coEvery { findNeighbourSeasonEpisodeUseCase.invoke(any(), any(), any(), any(), any()) } returns null
        createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onAction(MediaDetailAction.SelectSeasonIndex(1))
            val uiState = awaitItem().mediaDetailUiState as TvShowMediaDetailUiState
            assertThat(uiState.selectedSeasonIndex).isEqualTo(1)
            skipItems(1)    // updated episodes
            verify { getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke(any(), "id1") }
        }
    }

    @Test
    fun `given preselected stream, on play default, start selected stream`() = runTest {
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(MovieMediaDetailUiState(movieMediaDetail)))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(
            StreamsUiState(listOf(stream), "ident")
        )
        coEvery { updateWatchHistoryOnStartStreamUseCase.invoke(any(), any(), any(), any())} returns 1L
        createViewModel()

        viewModel.event.test {
            viewModel.onAction(MediaDetailAction.PlayDefault)
            val event = awaitItem()
            val expected = MediaDetailEvent.PlayStream(
                PlayStreamParams("ident", 1L, PlayerMediaInfo.Movie(movieMediaDetail.title), false)
            )
            assertThat(event).isEqualTo(expected)
        }
    }

    @Test
    fun `given tv show media detail, when episode selection changed, poster is updated`() = runTest {
        val tvShowUiState = TvShowMediaDetailUiState(
            tvShow = tvShowMediaDetail,
            seasons = listOf(createSeason(0), createSeason(1)),
            selectedSeasonIndex = 0,
        )
        every { getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke(any(), any()) } returns
                flowOf(Resource.Success(TvShowSeasonEpisodes(tvShowSeasonEpisodes, 0)))
        every { getMediaDetailUiStateUseCase.invoke(any()) } returns flowOf(Resource.Success(tvShowUiState))
        every { getStreamsUiStateUseCase.invoke(any(), any(), any()) } returns flowOf(
            StreamsUiState(listOf(stream), null)
        )
        coEvery { findNeighbourSeasonEpisodeUseCase.invoke(any(), any(), any(), any(), any()) } returns null
        createViewModel()

        viewModel.uiState.test {
            skipItems(1)    // initial state
            viewModel.onAction(MediaDetailAction.SelectEpisodeIndex(1))
            skipItems(2)    // selected index & streams
            val uiState = awaitItem().mediaDetailUiState as TvShowMediaDetailUiState
            assertThat(uiState.posterData?.episodeName).isEqualTo("title1")
        }
    }

    private fun createViewModel() {
        viewModel = MediaDetailViewModel(
            savedStateHandle,
            getMediaDetailUiStateUseCase,
            getSeasonEpisodesWithWatchHistoryUpdatesUseCase,
            updateWatchHistoryOnStartStreamUseCase,
            getStreamsUiStateUseCase,
            findNeighbourSeasonEpisodeUseCase,
            getSeasonEpisodesUseCase
        )
    }

    companion object {
        private const val MEDIA_ID = "mediaId"

        private val movieMediaDetail = MediaDetailMovie(
            "id",
            "title",
            "originalTite",
            "img",
            "2023",
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            "plot",
            100,
            null,
        )
        private val tvShowMediaDetail = MediaDetailTvShow(
            "id",
            "title",
            "originalTite",
            "img",
            "2023",
            emptyList(),
            "plot",
            emptyList(),
            null,
            4,
            100,
            null,
        )
        private val stream = MediaStream("ident")
        private val tvShowSeasonEpisodes = listOf(
            createEpisode(0),
            createEpisode(1),
            createEpisode(2),
        )

        private fun createEpisode(index: Int) = TvShowEpisode(
            "id$index",
            index,
            "title$index",
            "original$index",
            null,
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            "",
            "",
            100
        )

        private fun createSeason(index: Int) = TvShowSeason(
            "id$index",
            index,
            "title$index",
            null,
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            "",
        )
    }
}