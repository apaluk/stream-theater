package com.apaluk.streamtheater.data.stream_cinema.local

import android.util.LruCache
import com.apaluk.streamtheater.domain.model.media.MediaStream

object MediaStreamsCache: LruCache<String, List<MediaStream>>(4)
