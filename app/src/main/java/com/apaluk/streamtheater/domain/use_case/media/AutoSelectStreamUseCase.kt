package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import javax.inject.Inject
import kotlin.math.abs

class AutoSelectStreamUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
    suspend operator fun invoke(mediaId: String?, streams: List<MediaStream>): MediaStream? {
        if(streams.isEmpty())
            return null

        if(streams.size == 1)
            return streams.first()

        val lastSelection = watchHistoryRepository.getLastSelectedStream(mediaId) ?: return null
        return streams.minByOrNull { abs(lastSelection.speed - it.speed) }
    }
}