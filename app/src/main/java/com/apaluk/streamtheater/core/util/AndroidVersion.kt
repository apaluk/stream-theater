package com.apaluk.streamtheater.core.util

import android.os.Build

object AndroidVersion {
    fun isR() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}
