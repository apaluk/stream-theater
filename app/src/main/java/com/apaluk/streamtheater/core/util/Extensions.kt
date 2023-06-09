package com.apaluk.streamtheater.core.util

fun requireNotNullOrEmpty(str: String?): String {
    require(!str.isNullOrEmpty())
    return str
}

fun Any?.isNullOrEmptyList(): Boolean {
    return this == null || (this is List<*> && isEmpty())
}

/** Makes `when` statement produce error if not exhaustive */
val <T> T.exhaustive: T
    get() = this