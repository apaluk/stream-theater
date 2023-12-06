package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.WatchHistoryEntry
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSelectedEpisodeUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getSelectedEpisodeUseCase: GetSelectedEpisodeUseCase

    @MockK
    private lateinit var watchHistoryRepository: WatchHistoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when watch history is empty, return 0`() = runTest {
        val episodes = (1..10).map(::createEpisode)
        every { watchHistoryRepository.getEpisodesWatchHistory(any(), any()) } returns flowOf(emptyList())
        createUseCase()

        val result = getSelectedEpisodeUseCase("", "", episodes)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `when last episode is not watched, return index of this episode`() = runTest {
        val episodes = (1..10).map(::createEpisode)
        val watchHistoryEntry1 = createWatchHistoryEntry("id2", false)
        val watchHistoryEntry2 = createWatchHistoryEntry("id1", false)
        every { watchHistoryRepository.getEpisodesWatchHistory(any(), any()) } returns flowOf(
            listOf(watchHistoryEntry1, watchHistoryEntry2)
        )
        createUseCase()

        val result = getSelectedEpisodeUseCase("", "", episodes)
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `when last episode is watched, return index of next episode`() = runTest {
        val episodes = (1..10).map(::createEpisode)
        every { watchHistoryRepository.getEpisodesWatchHistory(any(), any()) } returns flowOf(
            listOf(createWatchHistoryEntry("id2", true))
        )
        createUseCase()

        val result = getSelectedEpisodeUseCase("", "", episodes)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `given some not watched episodes, when last episode is watched, return first not watched episode`() = runTest {
        val episodes = (1..3).map(::createEpisode)
        every { watchHistoryRepository.getEpisodesWatchHistory(any(), any()) } returns flowOf(
            listOf(
                createWatchHistoryEntry("id1", true),
                createWatchHistoryEntry("id3", false),
            )
        )
        createUseCase()

        val result = getSelectedEpisodeUseCase("", "", episodes)
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `when all episodes watched, return null`() = runTest {
        val episodes = (1..3).map(::createEpisode)
        every { watchHistoryRepository.getEpisodesWatchHistory(any(), any()) } returns flowOf(
            listOf(
                createWatchHistoryEntry("id3", true),
                createWatchHistoryEntry("id2", true),
                createWatchHistoryEntry("id1", true),
            )
        )
        createUseCase()

        val result = getSelectedEpisodeUseCase("", "", episodes)
        assertThat(result).isNull()
    }

    private fun createUseCase() {
        getSelectedEpisodeUseCase = GetSelectedEpisodeUseCase(watchHistoryRepository)
    }

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

    private fun createWatchHistoryEntry(episodeId: String, isWatched: Boolean) = WatchHistoryEntry(
        0,
        "",
        null,
        episodeId,
        0,
        0,
        0,
        isWatched
    )
}