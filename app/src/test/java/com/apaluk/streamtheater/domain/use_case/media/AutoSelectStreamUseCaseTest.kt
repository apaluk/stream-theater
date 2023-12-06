package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AutoSelectStreamUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var autoSelectStreamUseCase: AutoSelectStreamUseCase

    @MockK
    private lateinit var watchHistoryRepository: WatchHistoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when streams is empty, return null`() = runTest {
        createUseCase()
        val result = autoSelectStreamUseCase.invoke("mediaId", emptyList())
        assertThat(result).isNull()
    }

    @Test
    fun `when streams size is 1, select this stream`() = runTest {
        createUseCase()
        val stream = MediaStream("id")
        val result = autoSelectStreamUseCase.invoke("mediaId", listOf(stream))
        assertThat(result).isEqualTo(stream)
    }

    @Test
    fun `given last selected stream, when streams size is 2, select stream with closest speed to last selected stream`() = runTest {
        val lastSelection = MediaStream("id3", speed = 1.2)
        coEvery { watchHistoryRepository.getLastSelectedStream(any()) } returns lastSelection
        createUseCase()

        val stream1 = MediaStream("id1", speed = 1.0)
        val stream2 = MediaStream("id2", speed = 2.0)
        val result = autoSelectStreamUseCase.invoke("mediaId", listOf(stream1, stream2))
        assertThat(result).isEqualTo(stream1)
    }

    private fun createUseCase() {
        autoSelectStreamUseCase = AutoSelectStreamUseCase(watchHistoryRepository)
    }
}