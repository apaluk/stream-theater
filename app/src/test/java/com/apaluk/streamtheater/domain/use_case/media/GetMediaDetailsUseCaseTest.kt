package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.domain.repository.MediaInfoRepository
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetMediaDetailsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getMediaDetailsUseCase: GetMediaDetailsUseCase

    @MockK
    private lateinit var streamCinemaRepository: StreamCinemaRepository

    @MockK(relaxed = true)
    private lateinit var mediaInfoRepository: MediaInfoRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when media details are fetched, save media info`() = runTest {
        val movie = MediaDetailMovie(
            "",
            "",
            null,
            null,
            null,
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            null,
            0
        )
        coEvery { streamCinemaRepository.getMediaDetails(any()) } returns flowOf(Resource.Success(movie))
        createUseCase()

        getMediaDetailsUseCase("id")
        coVerify { mediaInfoRepository.upsertMediaInfo(any()) }
    }


    private fun createUseCase() {
        getMediaDetailsUseCase = GetMediaDetailsUseCase(
            streamCinemaRepository,
            mediaInfoRepository
        )
    }
}