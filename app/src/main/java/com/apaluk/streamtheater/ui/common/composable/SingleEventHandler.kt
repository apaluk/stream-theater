package com.apaluk.streamtheater.ui.common.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.apaluk.streamtheater.core.util.SingleEvent

@Composable
fun <T> SingleEventHandler(
    singleEvent: SingleEvent<T>,
    handler: (T) -> Unit
) {
    LaunchedEffect(singleEvent) {
        singleEvent.flow.collect {
            handler(it)
        }
    }
}