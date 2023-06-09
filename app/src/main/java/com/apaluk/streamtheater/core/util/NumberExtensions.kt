package com.apaluk.streamtheater.core.util

fun Int.withLeadingZeros(padding: Int): String =
    toString().padStart(padding, '0')