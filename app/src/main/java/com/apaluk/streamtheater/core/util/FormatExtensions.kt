package com.apaluk.streamtheater.core.util

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow

fun Long.formatFileSize(): String {
    if (this <= 0) return "0"
    val units = arrayOf(" B", " kB", " MB", " GB", " TB")
    val digitGroups = min((log10(this.toDouble()) / log10(1024.0)).toInt(), units.lastIndex)
    return DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())) + units[digitGroups]
}

fun Double.formatTransferSpeed(): String {
    if (this <= 0) return "0"
    val speedInMBits = 8.0 * toDouble() / 1024.0 / 1024.0
    return DecimalFormat("#,##0.#").format(speedInMBits) + " Mbits/s"
}

fun Int.formatDuration(): String {
    val seconds = this % 60
    val minutes = (this / 60) % 60
    val hours = (this / (60 * 60))
    return if(hours > 0)
        "${"%d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    else
        "${"%02d".format(minutes)}:${"%02d".format(seconds)}"
}