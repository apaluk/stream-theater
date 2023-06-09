package com.apaluk.streamtheater.data.database.repository

import com.apaluk.streamtheater.core.util.mapList
import com.apaluk.streamtheater.core.util.nowInMillis
import com.apaluk.streamtheater.data.database.dao.WatchHistoryDao
import com.apaluk.streamtheater.data.database.model.WatchHistory
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.domain.model.media.WatchHistoryEntry
import com.apaluk.streamtheater.domain.model.media.util.toStream
import com.apaluk.streamtheater.domain.model.media.util.toWatchHistoryEntry
import com.apaluk.streamtheater.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchHistoryRepositoryImpl(
    private val watchHistoryDao: WatchHistoryDao
): WatchHistoryRepository {

    override fun getMediaWatchHistory(mediaId: String): Flow<List<WatchHistoryEntry>> =
        watchHistoryDao.getMediaWatchHistory(mediaId)
            .mapList { it.toWatchHistoryEntry() }

    override fun getTvShowEpisodeWatchHistory(mediaId: String): Flow<List<WatchHistoryEntry>> =
        watchHistoryDao.getTvShowEpisodeWatchHistory(mediaId)
            .mapList { it.toWatchHistoryEntry() }

    override suspend fun ensureStream(mediaStream: MediaStream): Long =
        watchHistoryDao.getStreamId(mediaStream.ident)
            ?: watchHistoryDao.insertStream(mediaStream.toStream())

    override suspend fun ensureWatchHistoryEntry(
        mediaId: String,
        season: String?,
        episode: String?,
        streamId: Long
    ): Long {
        val newWatchHistory = watchHistoryDao.getWatchHistoryEntry(mediaId, season, episode)?.copy(
                streamId = streamId,
                lastUpdate = nowInMillis()
            ) ?: run {
            WatchHistory(
                id = 0L,
                mediaId = mediaId,
                seasonId = season,
                episodeId = episode,
                streamId = streamId,
                progressSeconds = 0,
                lastUpdate = nowInMillis(),
                isWatched = false
            )
        }
        return watchHistoryDao.upsertWatchHistoryEntry(newWatchHistory)
    }

    override suspend fun updateWatchHistoryProgress(
        watchHistoryId: Long,
        progress: Int,
        isWatched: Boolean
    ) {
        val updated = watchHistoryDao.getWatchHistoryEntry(watchHistoryId)?.copy(
            progressSeconds = progress,
            isWatched = isWatched
        ) ?: return
        watchHistoryDao.upsertWatchHistoryEntry(updated)
    }

    override fun getEpisodesWatchHistory(
        mediaId: String,
        season: String?
    ): Flow<List<WatchHistoryEntry>> =
        watchHistoryDao.getSeasonEpisodesWatchHistoryEntries(mediaId, season)
            .mapList { it.toWatchHistoryEntry() }

    override suspend fun getStreamIdent(streamId: Long): String? =
        watchHistoryDao.getStreamIdent(streamId)

    override suspend fun getWatchHistoryById(watchHistoryId: Long): WatchHistoryEntry? =
        watchHistoryDao.getWatchHistoryById(watchHistoryId)?.toWatchHistoryEntry()

    override fun getLastWatchedMedia(): Flow<List<WatchHistoryEntry>> =
        watchHistoryDao.getLastWatchedMediaIds()
            .map { list ->
                list.mapNotNull { mediaId ->
                    watchHistoryDao.getLatestWatchHistoryEntry(mediaId)
                }.map { it.toWatchHistoryEntry() }
            }

    override suspend fun removeWatchHistoryEntry(mediaId: String) {
        watchHistoryDao.removeFromWatchHistory(mediaId)
    }
}