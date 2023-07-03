package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.StreamsMediaType
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.ui.media_detail.StreamsUiState
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetStreamsUiStateUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val watchHistoryRepository: WatchHistoryRepository,
    private val autoSelectStream: AutoSelectStreamUseCase
) {
    operator fun invoke(mediaId: String, parentMediaId: String, streamsMediaType: StreamsMediaType): Flow<StreamsUiState?> = flow {
        // get media streams from StreamCinema API
        val streams = streamCinemaRepository.getMediaStreams(mediaId).last().data?.sortedBy { stream -> stream.speed }
            ?: run {
                emit(null)
                return@flow
            }
        emit(StreamsUiState(streams = streams))

        // create watch history flow for further uses
        val watchHistoryFlow = when (streamsMediaType) {
            StreamsMediaType.Movie -> watchHistoryRepository.getMediaWatchHistory(mediaId)
            StreamsMediaType.TvShowEpisode -> watchHistoryRepository.getTvShowEpisodeWatchHistory(mediaId)
        }

        // get last watched stream of this media (if any)
        val lastWatchedStream = watchHistoryFlow.firstOrNull()?.firstOrNull()
        if(lastWatchedStream != null) {
            emit(StreamsUiState(
                streams = streams,
                selectedStreamId = watchHistoryRepository.getStreamIdent(lastWatchedStream.streamId)
            ))
        } else {
            // if no last watched stream, try auto select stream
            autoSelectStream(parentMediaId, streams)?.let { stream ->
                emit(StreamsUiState(
                    streams = streams,
                    selectedStreamId = stream.ident
                ))
            }
        }
        // continue emitting watch history updates
        emitAll(
            watchHistoryFlow
                .mapNotNull { it.firstOrNull() }
                .distinctUntilChanged()
                .map {
                    StreamsUiState(
                        streams = streams,
                        selectedStreamId = watchHistoryRepository.getStreamIdent(it.streamId)
                    )
                }
        )
    }
}

