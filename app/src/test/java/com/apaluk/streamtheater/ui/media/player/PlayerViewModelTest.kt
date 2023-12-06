package com.apaluk.streamtheater.ui.media.player

import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.use_case.media.GetStartFromPositionUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnVideoProgressUseCase
import com.apaluk.streamtheater.domain.use_case.webshare.GetFileLinkUseCase
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.media.media_detail.PlayStreamParams
import com.apaluk.streamtheater.ui.media.media_detail.util.PlayerMediaInfo
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PlayerViewModel

    @MockK
    private lateinit var getFileLinkUseCase: GetFileLinkUseCase

    @MockK
    private lateinit var updateWatchHistoryOnVideoProgressUseCase: UpdateWatchHistoryOnVideoProgressUseCase

    @MockK
    private lateinit var getStartFromPositionUseCase: GetStartFromPositionUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when params set, correct ui state is emitted`() = runTest {
        coEvery { getFileLinkUseCase.invoke(any()) } returns flowOf(Resource.Success("url"))
        coEvery { getStartFromPositionUseCase.invoke(any()) } returns 10
        createViewModel()

        val mediaInfo = PlayerMediaInfo.TvShow("title", null, null)
        viewModel.uiState.test {
            assertThat(awaitItem().uiState).isEqualTo(UiState.Loading)
            viewModel.setParams(PlayStreamParams("ident", 0L, mediaInfo, false))
            val uiState = awaitItem()
            assertThat(uiState.uiState).isEqualTo(UiState.Content)
            assertThat(uiState.videoUrl).isEqualTo("url")
            assertThat(uiState.seekToPositionInSeconds).isIn(1L..10L)
            assertThat(uiState.playerMediaInfo).isEqualTo(mediaInfo)
        }
    }

    @Test
    fun `given getFileLink error, when params set, error ui state is emitted`() = runTest {
        coEvery { getFileLinkUseCase.invoke(any()) } returns flowOf(Resource.Error("error"))
        createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.setParams(PlayStreamParams("ident", 0L, null, false))
            val uiState = awaitItem()
            assertThat(uiState.uiState).isEqualTo(UiState.Error("error"))
        }
    }

    @Test
    fun `when video ended, navigate up is triggered`() = runTest {
        createViewModel()

        viewModel.event.test {
            viewModel.onAction(PlayerScreenAction.VideoEnded)
            assertThat(awaitItem()).isEqualTo(PlayerScreenEvent.NavigateUp)
        }
    }

    @Test
    fun `when video progress updated, update watch history`() = runTest {
        coEvery { getFileLinkUseCase.invoke(any()) } returns flowOf(Resource.Success("url"))
        coEvery { getStartFromPositionUseCase.invoke(any()) } returns 10
        coEvery { updateWatchHistoryOnVideoProgressUseCase.invoke(any(), any(), any()) } just Runs
        createViewModel()

        viewModel.setParams(PlayStreamParams("ident", 0L, null, false))
        viewModel.onAction(PlayerScreenAction.VideoProgressChanged(VideoProgress(10, 100)))
        coVerify { updateWatchHistoryOnVideoProgressUseCase.invoke(any(), 10, 100) }
    }

    @Test
    fun `given prev-next buttons enabled, when player controls visible, show prev-next controls`() = runTest {
        coEvery { getFileLinkUseCase.invoke(any()) } returns flowOf(Resource.Success("url"))
        coEvery { getStartFromPositionUseCase.invoke(any()) } returns 0
        createViewModel()

        viewModel.setParams(PlayStreamParams("ident", 0L, null, true))
        viewModel.uiState.test {
            skipItems(1)
            viewModel.onAction(PlayerScreenAction.PlayerControlsVisibilityChanged(true))
            assertThat(awaitItem().showNextPrevButtons).isTrue()
        }
    }
    @Test
    fun `given prev-next buttons disabled, when player controls visible, do not show prev-next controls`() = runTest {
        coEvery { getFileLinkUseCase.invoke(any()) } returns flowOf(Resource.Success("url"))
        coEvery { getStartFromPositionUseCase.invoke(any()) } returns 0
        createViewModel()

        viewModel.setParams(PlayStreamParams("ident", 0L, null, false))
        viewModel.uiState.test {
            assertThat(awaitItem().showNextPrevButtons).isFalse()
            viewModel.onAction(PlayerScreenAction.PlayerControlsVisibilityChanged(true))
            ensureAllEventsConsumed()
        }
    }

    private fun createViewModel() {
        viewModel = PlayerViewModel(
            getFileLinkUseCase,
            updateWatchHistoryOnVideoProgressUseCase,
            getStartFromPositionUseCase
        )
    }
}