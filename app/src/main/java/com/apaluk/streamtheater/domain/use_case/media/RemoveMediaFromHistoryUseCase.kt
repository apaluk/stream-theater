package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import javax.inject.Inject

class RemoveMediaFromHistoryUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
        suspend operator fun invoke(dashboardMedia: DashboardMedia) {
            dashboardMedia.mediaId?.let {
                watchHistoryRepository.removeWatchHistoryEntry(it)
            }
        }
}