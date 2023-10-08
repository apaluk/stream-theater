package com.apaluk.streamtheater.domain.use_case.media

import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.mockkResourcesManager
import com.apaluk.streamtheater.domain.model.media.MediaProgress
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSeasonEpisodesWithWatchHistoryUpdatesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getSeasonEpisodesWithWatchHistoryUpdatesUseCase: GetSeasonEpisodesWithWatchHistoryUpdatesUseCase

    @MockK
    private lateinit var getSeasonEpisodesUseCase: GetSeasonEpisodesUseCase

    @MockK
    private lateinit var getSelectedEpisodeUseCase: GetSelectedEpisodeUseCase

    @MockK
    private lateinit var getEpisodesWithWatchHistoryUpdatesUseCase: GetEpisodesWithWatchHistoryUpdatesUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when get episodes returns error, emit error`() = runTest {
        coEvery { getSeasonEpisodesUseCase.invoke(any()) } returns Resource.Error()
        createUseCase()

        val result = getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke("mediaId", "seasonId").last()
        assertThat(result).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun `given episodes, watch history and last selected episode, when get episodes, return episodes with updates`() = runTest {
        val episode1 = createEpisode(1)
        val episode2 = createEpisode(2)
        coEvery { getSeasonEpisodesUseCase.invoke(any()) } returns Resource.Success(listOf(createEpisode(1), createEpisode(2)))
        coEvery { getSelectedEpisodeUseCase.invoke(any(), any(), any()) } returns 1
        coEvery { getEpisodesWithWatchHistoryUpdatesUseCase.invoke(any(), any(), any()) } returns flowOf(
            listOf(
                episode1.copy(progress = MediaProgress(100, false)),
                episode2.copy(progress = MediaProgress(50, false))
            )
        )
        createUseCase()

        getSeasonEpisodesWithWatchHistoryUpdatesUseCase.invoke("mediaId", "seasonId").test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val resultWithHistory = awaitItem() as Resource.Success
            assertThat(resultWithHistory.data?.episodes?.first()?.id).isEqualTo("id1")
            assertThat(resultWithHistory.data?.episodes?.first()?.progress?.progressSeconds).isEqualTo(100)
            awaitComplete()
        }
    }

    private fun createUseCase() {
        getSeasonEpisodesWithWatchHistoryUpdatesUseCase = GetSeasonEpisodesWithWatchHistoryUpdatesUseCase(
            getSeasonEpisodesUseCase,
            getSelectedEpisodeUseCase,
            getEpisodesWithWatchHistoryUpdatesUseCase,
            mockkResourcesManager()
        )
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
}