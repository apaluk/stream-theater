package com.apaluk.streamtheater.ui.common.util

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.apaluk.streamtheater.BuildConfig

@Composable
@ReadOnlyComposable
fun stringResourceSafe(@StringRes id: Int): String =
    if (BuildConfig.DEBUG) {
        val resources = LocalContext.current.resources
        try {
            resources.getString(id)
        } catch (e: Resources.NotFoundException) {
            "missing res"
        }
    } else {
        stringResource(id)
    }

@Composable
@ReadOnlyComposable
fun stringResourceSafe(@StringRes id: Int, vararg formatArgs: Any): String =
    if (BuildConfig.DEBUG) {
        val resources = LocalContext.current.resources
        try {
            resources.getString(id, *formatArgs)
        } catch (e: Resources.NotFoundException) {
            "missing res"
        }
    } else {
        stringResource(id, *formatArgs)
    }