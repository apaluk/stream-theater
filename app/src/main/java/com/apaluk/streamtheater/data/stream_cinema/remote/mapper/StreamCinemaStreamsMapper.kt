package com.apaluk.streamtheater.data.stream_cinema.remote.mapper

import com.apaluk.streamtheater.core.util.requireNotNullOrEmpty
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.MediaStreamsResponseItemDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.SubtitleDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.VideoDto
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.Subtitles
import com.apaluk.streamtheater.domain.model.media.VideoDefinition
import javax.inject.Inject

class StreamCinemaStreamsMapper @Inject constructor() {

    fun toMediaStream(mediaStreamsResponseItemDto: MediaStreamsResponseItemDto): MediaStream {
        with (mediaStreamsResponseItemDto) {
            val duration = video.firstOrNull()?.duration?.toInt() ?: 0
            return MediaStream(
                ident = ident,
                size = size,
                duration = duration,
                speed = if (duration == 0) 0.0 else size.toDouble() / duration.toDouble(),
                audios = audio
                    .map { it.language.orEmpty() }
                    .filter { it.isNotBlank() }
                    .distinct(),
                video = video.firstOrNull()?.toVideoDefinition() ?: VideoDefinition.SD,
                subtitles = subtitles.mapNotNull { it.toSubtitles() }
            )
        }
    }

    private fun VideoDto.toVideoDefinition(): VideoDefinition =
        when {
            height > 2500 -> VideoDefinition.UHD_8K
            height > 2000 -> VideoDefinition.UHD_4K
            height > 900 -> VideoDefinition.FHD
            height > 600 -> VideoDefinition.HD
            else -> VideoDefinition.SD
        }

    private fun SubtitleDto.toSubtitles(): Subtitles? =
        try {
            Subtitles(
                lang = requireNotNullOrEmpty(language),
                forced = forced ?: false
            )
        } catch (e: Exception) {
            null
        }
}