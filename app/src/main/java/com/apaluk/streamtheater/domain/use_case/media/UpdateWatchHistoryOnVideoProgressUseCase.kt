package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import javax.inject.Inject

class UpdateWatchHistoryOnVideoProgressUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
    suspend operator fun invoke(
        watchHistoryId: Long,
        progressSeconds: Int,
        totalDurationSeconds: Int
    ) {
        val timeToEndSeconds = totalDurationSeconds - progressSeconds
        val relativeProgress = progressSeconds.toFloat() / totalDurationSeconds.toFloat()
        val isWatched = (timeToEndSeconds < 90 && relativeProgress > 0.9f) || relativeProgress > 0.96f
        watchHistoryRepository.updateWatchHistoryProgress(
            watchHistoryId, progressSeconds, isWatched
        )
    }
}