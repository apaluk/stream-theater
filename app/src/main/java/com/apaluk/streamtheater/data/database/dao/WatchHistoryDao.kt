package com.apaluk.streamtheater.data.database.dao

import androidx.room.*
import com.apaluk.streamtheater.data.database.model.Stream
import com.apaluk.streamtheater.data.database.model.WatchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Query("""
        SELECT * FROM watchHistory 
        WHERE mediaId=:mediaId 
        ORDER BY lastUpdate DESC
        """)
    fun getMediaWatchHistory(mediaId: String): Flow<List<WatchHistory>>

    @Query("""
        SELECT * FROM watchHistory 
        WHERE episodeId=:episodeId 
        ORDER BY lastUpdate DESC
        """)
    fun getTvShowEpisodeWatchHistory(episodeId: String): Flow<List<WatchHistory>>

    @Query("SELECT * FROM watchHistory WHERE id=:watchHistoryId")
    suspend fun getWatchHistoryById(watchHistoryId: Long): WatchHistory?

    @Query("SELECT id FROM streams WHERE ident=:streamIdent")
    suspend fun getStreamId(streamIdent: String): Long?

    @Insert
    suspend fun insertStream(stream: Stream): Long

    @Query("""
        SELECT * FROM watchHistory 
        WHERE mediaId=:mediaId 
            AND (seasonId=:season OR (seasonId is NULL AND :season is NULL)) 
            AND (episodeId=:episode OR (episodeId is NULL AND :episode is NULL))
        """)
    suspend fun getWatchHistoryEntry(mediaId: String, season: String?, episode: String?): WatchHistory?

    @Query("SELECT * FROM watchHistory WHERE id=:watchHistoryId")
    suspend fun getWatchHistoryEntry(watchHistoryId: Long): WatchHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchHistoryEntry(watchHistory: WatchHistory): Long

    @Query("""
        SELECT * FROM watchHistory
        WHERE mediaId=:mediaId AND (seasonId=:season OR (seasonId is NULL AND :season is NULL))
        ORDER BY lastUpdate DESC
    """)
    fun getSeasonEpisodesWatchHistoryEntries(mediaId: String, season: String?): Flow<List<WatchHistory>>

    @Query("SELECT ident FROM streams WHERE id=:streamId")
    suspend fun getStreamIdent(streamId: Long): String?

    @Query("""
        SELECT * FROM watchHistory AS wh1
        WHERE lastUpdate = (
            SELECT MAX(lastUpdate) FROM watchHistory AS wh2
            WHERE wh1.mediaId = wh2.mediaId
        )
        ORDER BY lastUpdate DESC
    """
    )
    fun getLastWatchedWatchHistoryEntries(): Flow<List<WatchHistory>>

    @Query("""
        DELETE FROM watchHistory
        WHERE mediaId=:mediaId
    """)
    suspend fun removeFromWatchHistory(mediaId: String)

    @Query("""
        SELECT streamId FROM watchHistory
        WHERE mediaId=:mediaId AND isAutoSelected=0
        ORDER BY lastUpdate DESC
        LIMIT 1
    """)
    suspend fun getLastUserSelectedStreamId(mediaId: String): Long?

    @Query("""
        SELECT streamId FROM watchHistory
        WHERE isAutoSelected=0
        ORDER BY lastUpdate DESC
        LIMIT 1
    """)
    suspend fun getLastUserSelectedStreamId(): Long?

    @Query("""
        SELECT * FROM streams
        WHERE id=:streamId
    """)
    suspend fun getStreamById(streamId: Long): Stream?
}