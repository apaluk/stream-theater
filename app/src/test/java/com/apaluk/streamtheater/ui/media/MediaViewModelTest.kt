package com.apaluk.streamtheater.ui.media

import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MediaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MediaViewModel

    @Before
    fun setUp() {
        viewModel = MediaViewModel()
    }

    @Test
    fun `when skip to previous video, then event is emitted`() = runTest {
        viewModel.event.test {
            viewModel.onAction(MediaAction.SkipToPreviousVideo)
            assertThat(awaitItem()).isEqualTo(MediaEvent.SkipToPreviousVideo)
        }
    }

    @Test
    fun `when skip to next video, then event is emitted`() = runTest {
        viewModel.event.test {
            viewModel.onAction(MediaAction.SkipToNextVideo)
            assertThat(awaitItem()).isEqualTo(MediaEvent.SkipToNextVideo)
        }
    }
}