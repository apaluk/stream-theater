package com.apaluk.streamtheater.core.util

fun List<String>.commaSeparatedList(maxItems: Int = Int.MAX_VALUE): String? {
    return if (isEmpty()) null else take(maxItems).joinToString(", ")
}