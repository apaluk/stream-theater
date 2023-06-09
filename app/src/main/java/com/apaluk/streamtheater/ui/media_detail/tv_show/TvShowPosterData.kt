package com.apaluk.streamtheater.ui.media_detail.tv_show

data class TvShowPosterData(
    val episodeNumber: String? = null,
    val episodeName: String? = null,
    val duration: Int? = null,
    val imageUrl: String? = null,
    val progress: Float = 0f,
    val showPlayButton: Boolean = false
)