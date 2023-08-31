package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.domain.model.media.StreamsMediaType
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import com.apaluk.streamtheater.ui.media.media_detail.StreamsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import javax.inject.Inject

class GetStreamsUiStateUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val watchHistoryRepository: WatchHistoryRepository,
    private val autoSelectStream: AutoSelectStreamUseCase
) {
    operator fun invoke(
        mediaId: String,
        parentMediaId: String,
        streamsMediaType: StreamsMediaType
    ): Flow<StreamsUiState?> = flow {
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
            emit(
                StreamsUiState(
                streams = streams,
                selectedStreamId = watchHistoryRepository.getStreamIdent(lastWatchedStream.streamId)
            )
            )
        } else {
            // if no last watched stream, try auto select stream
            autoSelectStream(parentMediaId, streams)?.let { stream ->
                emit(
                    StreamsUiState(
                        streams = streams,
                        selectedStreamId = stream.ident
                    )
                )
            }
        }
        // continue emitting watch history updates
        emitAll(
            watchHistoryFlow
                .mapNotNull { it.firstOrNull()?.streamId }
                .distinctUntilChanged()
                .map { streamId ->
                    Timber.d("xxx streams ui state streamId: $streamId")
                    StreamsUiState(
                        streams = streams,
                        selectedStreamId = watchHistoryRepository.getStreamIdent(streamId)
                    )
                }
        )
    }
}

