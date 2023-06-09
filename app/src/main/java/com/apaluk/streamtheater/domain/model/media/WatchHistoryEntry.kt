package com.apaluk.streamtheater.domain.model.media

data class WatchHistoryEntry(
    val id: Long,
    val mediaId: String,
    val seasonId: String?,
    val episodeId: String?,
    val streamId: Long,
    val progressSeconds: Int,
    val lastUpdate: Long,
    val isWatched: Boolean
)