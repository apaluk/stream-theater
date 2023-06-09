package com.apaluk.streamtheater.ui.media_detail.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.apaluk.streamtheater.ui.theme.success

object StColors {
    val pausedMedia: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0x9985A7E3) else Color(0x9985A7E3)

    val watchedMedia: Color
        @Composable
        get() = MaterialTheme.colorScheme.success.copy(alpha = 0.7f)
}

