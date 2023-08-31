package com.apaluk.streamtheater.core.util

import android.util.LruCache

class CacheControl<K, D>(
    val key: K,
    val cache: LruCache<K, D>
) {
    fun read(): D? = cache.get(key)

    fun write(data: D) {
        cache.put(key, data)
    }
}
