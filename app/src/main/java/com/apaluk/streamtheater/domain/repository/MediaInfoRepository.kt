package com.apaluk.streamtheater.domain.repository

import com.apaluk.streamtheater.domain.model.media.MediaBrief

interface MediaInfoRepository {
    suspend fun getMediaInfo(mediaId: String): MediaBrief?
    suspend fun upsertMediaInfo(mediaBrief: MediaBrief)
}