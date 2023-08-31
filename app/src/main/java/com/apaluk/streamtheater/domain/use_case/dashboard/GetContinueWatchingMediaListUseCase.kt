package com.apaluk.streamtheater.domain.use_case.dashboard

import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.mapList
import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.domain.model.media.util.toDashboardMedia
import com.apaluk.streamtheater.domain.repository.MediaInfoRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.domain.use_case.media.GetMediaDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetContinueWatchingMediaListUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository,
    private val mediaInfoRepository: MediaInfoRepository,
    private val getMediaDetails: GetMediaDetailsUseCase
) {

    operator fun invoke(): Flow<Resource<List<DashboardMedia>>> = flow {
        emit(Resource.Loading())
        val lastInProgressMedia = watchHistoryRepository.getLastWatchedMedia().first()
        val continueWatchingLocal = lastInProgressMedia.map {
            mediaInfoRepository.getMediaInfo(it.mediaId)?.toDashboardMedia() ?: DashboardMedia()
        }
        emit(Resource.Success(continueWatchingLocal))
        emitAll(watchHistoryRepository.getLastWatchedMedia()
            .mapList { it.mediaId }
            .distinctUntilChanged()
            .map { list ->
                list.mapNotNull {
                    getMediaDetails(it).data?.toDashboardMedia()
                }
            }.map { Resource.Success(it) }
        )
    }
}