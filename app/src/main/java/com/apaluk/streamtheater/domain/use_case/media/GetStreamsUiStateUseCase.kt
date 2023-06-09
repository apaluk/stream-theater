package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.StreamsMediaType
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.ui.media_detail.StreamsUiState
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetStreamsUiStateUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val watchHistoryRepository: WatchHistoryRepository
) {
    operator fun invoke(mediaId: String, streamsMediaType: StreamsMediaType): Flow<StreamsUiState?> = flow {
        val streams = streamCinemaRepository.getMediaStreams(mediaId).last().data?.sortedBy { stream -> stream.size }
            ?: run {
                emit(null)
                return@flow
            }
        val watchHistoryFlow = when (streamsMediaType) {
            StreamsMediaType.Movie -> watchHistoryRepository.getMediaWatchHistory(mediaId)
            StreamsMediaType.TvShowEpisode -> watchHistoryRepository.getTvShowEpisodeWatchHistory(mediaId)
        }
        emitAll(
            watchHistoryFlow
                .map { it.firstOrNull() }
                .distinctUntilChanged()
                .map {
                    StreamsUiState(
                        streams = streams,
                        selectedStreamId = watchHistoryRepository.getStreamIdent(it?.streamId)
                    )
                }
        )
    }
}

private suspend fun WatchHistoryRepository.getStreamIdent(streamId: Long?): String? =
    if (streamId == null) null else getStreamIdent(streamId)
