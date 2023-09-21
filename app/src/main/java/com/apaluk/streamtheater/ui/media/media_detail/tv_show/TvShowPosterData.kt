package com.apaluk.streamtheater.ui.media.media_detail.tv_show

import com.apaluk.streamtheater.ui.media.media_detail.common.MediaDetailPosterMainAction

data class TvShowPosterData(
    val episodeNumber: String? = null,
    val episodeName: String? = null,
    val duration: Int? = null,
    val imageUrl: String? = null,
    val progress: Float = 0f,
    val mainButtonAction: MediaDetailPosterMainAction = MediaDetailPosterMainAction.None,
)