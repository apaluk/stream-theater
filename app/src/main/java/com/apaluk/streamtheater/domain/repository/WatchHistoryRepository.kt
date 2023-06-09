package com.apaluk.streamtheater.domain.repository

import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.WatchHistoryEntry
import kotlinx.coroutines.flow.Flow

interface WatchHistoryRepository {

    fun getMediaWatchHistory(mediaId: String): Flow<List<WatchHistoryEntry>>
    fun getTvShowEpisodeWatchHistory(mediaId: String): Flow<List<WatchHistoryEntry>>
    suspend fun getWatchHistoryById(watchHistoryId: Long): WatchHistoryEntry?
    suspend fun ensureStream(mediaStream: MediaStream): Long
    suspend fun ensureWatchHistoryEntry(mediaId: String, season: String?, episode: String?, streamId: Long): Long
    suspend fun updateWatchHistoryProgress(watchHistoryId: Long, progress: Int, isWatched: Boolean)
    fun getEpisodesWatchHistory(mediaId: String, season: String?): Flow<List<WatchHistoryEntry>>
    suspend fun getStreamIdent(streamId: Long): String?
    fun getLastWatchedMedia(): Flow<List<WatchHistoryEntry>>
    suspend fun removeWatchHistoryEntry(mediaId: String)
}