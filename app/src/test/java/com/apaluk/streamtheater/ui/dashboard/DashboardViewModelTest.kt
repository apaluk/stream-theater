package com.apaluk.streamtheater.ui.dashboard

import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.domain.use_case.dashboard.GetContinueWatchingMediaListUseCase
import com.apaluk.streamtheater.domain.use_case.media.RemoveMediaFromHistoryUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel

    @MockK
    private lateinit var getContinueWatchingMediaList: GetContinueWatchingMediaListUseCase

    @MockK(relaxed = true)
    private lateinit var removeMediaFromHistory: RemoveMediaFromHistoryUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `given empty continue watching list, when loading, navigate to search`() = runTest {
        every { getContinueWatchingMediaList.invoke() } returns flowOf(Resource.Success(emptyList()))
        createViewModel()

        viewModel.event.test {
            assertThat(awaitItem()).isEqualTo(DashboardEvent.NavigateToSearch)
        }
    }

    @Test
    fun `given non empty continue watching list, when loading, do not navigate to search`() = runTest {
        val dashboardResource = Resource.Success(listOf(DashboardMedia()))
        every { getContinueWatchingMediaList.invoke() } returns flowOf(dashboardResource)
        createViewModel()

        viewModel.event.test {
            expectNoEvents()
        }
    }

    @Test
    fun `given media id, when navigating to media detail, navigate to media detail`() = runTest {
        val dashboardResource = Resource.Success(listOf(DashboardMedia()))
        every { getContinueWatchingMediaList.invoke() } returns flowOf(dashboardResource)
        createViewModel()

        val mediaId = "mediaId"
        viewModel.event.test {
            viewModel.onAction(DashboardAction.GoToMediaDetail(DashboardMedia(mediaId = mediaId)))
            assertThat(awaitItem()).isEqualTo(DashboardEvent.NavigateToMediaDetail(mediaId))
        }
    }

    @Test
    fun `given media, removing media from continue watching list, triggers remove media from history`() = runTest {
        val dashboardMedia = DashboardMedia(mediaId = "mediaId")
        val dashboardResource = Resource.Success(listOf(dashboardMedia))
        every { getContinueWatchingMediaList.invoke() } returns flowOf(dashboardResource)
        createViewModel()

        viewModel.onAction(DashboardAction.RemoveFromList(dashboardMedia))
        coVerify { removeMediaFromHistory.invoke(dashboardMedia) }
    }

    private fun createViewModel() {
        viewModel = DashboardViewModel(getContinueWatchingMediaList, removeMediaFromHistory)
    }
}