package com.apaluk.streamtheater.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ColorScheme.success: Color
    @Composable
    get() = if (isSystemInDarkTheme()) md_theme_dark_success else md_theme_light_success
