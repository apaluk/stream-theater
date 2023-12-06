package com.apaluk.streamtheater.domain.use_case.media

import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.StreamsMediaType
import com.apaluk.streamtheater.domain.model.media.VideoDefinition
import com.apaluk.streamtheater.domain.model.media.WatchHistoryEntry
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.ui.media.media_detail.StreamsUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetStreamsUiStateUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getStreamsUiStateUseCase: GetStreamsUiStateUseCase

    @MockK
    private lateinit var streamCinemaRepository: StreamCinemaRepository

    @MockK
    private lateinit var watchHistoryRepository: WatchHistoryRepository

    @MockK
    private lateinit var autoSelectedStreamUseCase: AutoSelectStreamUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when no streams fetched, emit null`() = runTest {
        every { streamCinemaRepository.getMediaStreams(any()) } returns flowOf(Resource.Error())
        createUseCase()

        getStreamsUiStateUseCase("", "", StreamsMediaType.Movie).test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }

    @Test
    fun `given movie and no watch history, when streams are fetched, auto select stream`() = runTest {
        val streams = listOf(createStream(1), createStream(2))
        every { streamCinemaRepository.getMediaStreams(any()) } returns flowOf(Resource.Success(streams))
        every { watchHistoryRepository.getMediaWatchHistory(any()) } returns flowOf(emptyList())
        coEvery { autoSelectedStreamUseCase(any(), any()) } returns createStream(2)
        createUseCase()

        getStreamsUiStateUseCase("", "", StreamsMediaType.Movie).test {
            skipItems(1)    // first emission without selected stream
            assertThat(awaitItem()).isEqualTo(StreamsUiState(streams = streams, selectedStreamId = "id2"))
            awaitComplete()
        }
    }

    @Test
    fun `given tv show episode with watch history, when streams are fetched, emit streams and selected stream`() = runTest {
        val streams = listOf(createStream(1), createStream(2))
        val watchHistory = listOf(createWatchHistory(1))
        every { streamCinemaRepository.getMediaStreams(any()) } returns flowOf(Resource.Success(streams))
        every { watchHistoryRepository.getTvShowEpisodeWatchHistory(any()) } returns flowOf(watchHistory)
        coEvery { autoSelectedStreamUseCase(any(), any()) } returns null
        coEvery { watchHistoryRepository.getStreamIdent(any()) } returns "ident1"
        createUseCase()

        getStreamsUiStateUseCase("", "", StreamsMediaType.TvShowEpisode).test {
            skipItems(1)    // first emission without selected stream
            assertThat(awaitItem()).isEqualTo(StreamsUiState(streams = streams, selectedStreamId = "ident1"))
            awaitComplete()
        }
    }

    private fun createUseCase() {
        getStreamsUiStateUseCase = GetStreamsUiStateUseCase(
            streamCinemaRepository,
            watchHistoryRepository,
            autoSelectedStreamUseCase
        )
    }

    private fun createStream(index: Int) = MediaStream(
        ident = "id$index",
        size = 0L,
        duration = 0,
        speed = 0.0,
        video = VideoDefinition.SD,
        audios = emptyList(),
        subtitles = emptyList()
    )

    @Suppress("SameParameterValue")
    private fun createWatchHistory(index: Int) = WatchHistoryEntry(
        id = index.toLong(),
        mediaId = "id$index",
        seasonId = null,
        episodeId = null,
        streamId = 0,
        progressSeconds = 0,
        lastUpdate = 0,
        isWatched = false
    )
}