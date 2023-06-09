package com.apaluk.streamtheater.ui.common.util

import androidx.compose.ui.graphics.Color
import com.apaluk.streamtheater.domain.model.media.VideoDefinition

fun VideoDefinition.color(): Color =
    when(this) {
        VideoDefinition.SD -> Color(124, 162, 167)
        VideoDefinition.HD -> Color(82, 132, 209)
        VideoDefinition.FHD -> Color(91, 224, 143)
        VideoDefinition.UHD_4K -> Color(242, 101, 101)
        VideoDefinition.UHD_8K -> Color(153, 95, 241)
    }