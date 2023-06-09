package com.apaluk.streamtheater.domain.model.media

data class MediaBrief(
    val id: Long,
    val mediaId: String,
    val title: String,
    val imageUrl: String?,
    val durationSeconds: Int,
)
