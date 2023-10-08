package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
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

class GetSelectedSeasonUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getSelectedSeasonUseCase: GetSelectedSeasonUseCase

    @MockK
    private lateinit var watchHistoryRepository: WatchHistoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when watch history is empty, return first season with order number 1`() = runTest {
        val seasons = (0..10).map(::createSeason)
        every { watchHistoryRepository.getMediaWatchHistory(any()) } returns flowOf(emptyList())
        createUseCase()

        val result = getSelectedSeasonUseCase("", seasons)
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `when watch history is not empty, return index of last watched season`() = runTest {
        val seasons = (0..10).map(::createSeason)
        every { watchHistoryRepository.getMediaWatchHistory(any()) } returns flowOf(
            listOf(
                createWatchHistoryEntry("id3"),
                createWatchHistoryEntry("id1")
            )
        )
        createUseCase()

        val result = getSelectedSeasonUseCase("", seasons)
        assertThat(result).isEqualTo(3)
    }

    private fun createUseCase() {
        getSelectedSeasonUseCase = GetSelectedSeasonUseCase(watchHistoryRepository)
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

    private fun createWatchHistoryEntry(seasonId: String) = WatchHistoryEntry(
        0,
        "",
        seasonId,
        null,
        0,
        0,
        0,
        false
    )
}