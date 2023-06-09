package com.apaluk.streamtheater.data.stream_cinema.remote.adapter

import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.TvShowChildType
import com.squareup.moshi.FromJson
import timber.log.Timber

class TvShowChildTypeAdapter {

    @FromJson
    fun fromJson(value: String): TvShowChildType {
        return when(value) {
            "season" -> TvShowChildType.Season
            "episode" -> TvShowChildType.Episode
            else -> {
                Timber.w("Unknown Tv show child type: $value")
                TvShowChildType.Episode
            }
        }
    }
}