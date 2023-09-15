package com.apaluk.streamtheater.core.resources

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.qualifiers.ApplicationContext

class ResourcesManagerImpl(
    @ApplicationContext private val applicationContext: Context,
) : ResourcesManager {
    private val applicationResources: Resources = applicationContext.resources

    @ColorInt
    override fun getColor(@ColorRes colorRes: Int): Int {
        return ResourcesCompat.getColor(applicationResources, colorRes, null)
    }

    override fun getString(@StringRes stringRes: Int): String {
        return applicationContext.getString(stringRes)
    }

    override fun getString(stringRes: Int, vararg arguments: Any): String {
        return applicationContext.getString(stringRes, *arguments)
    }
}
