package com.apaluk.streamtheater.ui.common.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> EventHandler(
    eventFlow: Flow<T>,
    handler: suspend (T) -> Unit,
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect {
            handler(it)
        }
    }
}