package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UpdateWatchHistoryOnVideoProgressUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var updateWatchHistoryOnVideoProgressUseCase: UpdateWatchHistoryOnVideoProgressUseCase

    @MockK(relaxed = true)
    private lateinit var watchHistoryRepository: WatchHistoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when progress is not at the end, update progress`() = runTest {
        createUseCase()
        updateWatchHistoryOnVideoProgressUseCase(1, 10, 100)
        coVerify { watchHistoryRepository.updateWatchHistoryProgress(1, 10, false) }
    }

    @Test
    fun `when progress is at the end, update progress and mark as watched`() = runTest {
        createUseCase()
        updateWatchHistoryOnVideoProgressUseCase(1, 96, 100)
        coVerify { watchHistoryRepository.updateWatchHistoryProgress(1, 96, true) }
    }

    private fun createUseCase() {
        updateWatchHistoryOnVideoProgressUseCase = UpdateWatchHistoryOnVideoProgressUseCase(watchHistoryRepository)
    }
}