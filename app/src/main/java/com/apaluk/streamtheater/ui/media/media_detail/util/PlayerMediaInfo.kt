package com.apaluk.streamtheater.ui.media.media_detail.util

sealed class PlayerMediaInfo {
    data class Movie(
        val title: String,
    ): PlayerMediaInfo()

    data class TvShow(
        val title: String,
        val seasonEpisode: String?,
        val episodeName: String?,
    ): PlayerMediaInfo()
}