package com.apaluk.streamtheater.domain.model.dashboard

data class DashboardMedia(
    val mediaId: String? = null,    // null mediaId for empty media
    val title: String? = null,
    val duration: Int? = null,
    val progressSeconds: Int? = null,
    val imageUrl: String? = null
)
