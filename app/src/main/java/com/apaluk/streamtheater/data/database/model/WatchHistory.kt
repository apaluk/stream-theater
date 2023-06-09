package com.apaluk.streamtheater.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchHistory")
data class WatchHistory(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val mediaId: String,
    val seasonId: String?,
    val episodeId: String?,
    val streamId: Long,
    val progressSeconds: Int,
    val lastUpdate: Long,
    val isWatched: Boolean
)