package com.apaluk.streamtheater.core.util

import kotlinx.coroutines.CancellationException

fun Exception.throwIfCancellation() {
    if(this is CancellationException)
        throw this
}