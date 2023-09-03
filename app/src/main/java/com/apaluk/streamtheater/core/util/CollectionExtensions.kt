package com.apaluk.streamtheater.core.util

fun List<String>.commaSeparatedList(maxItems: Int = -1): String? {
    return if (isEmpty()) null else take(maxItems).joinToString(", ")
}