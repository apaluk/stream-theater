package com.apaluk.streamtheater.core.resources

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface ResourcesManager {

    @ColorInt fun getColor(@ColorRes colorRes: Int): Int
    fun getString(@StringRes stringRes: Int): String
    fun getString(stringRes: Int, vararg arguments: Any): String

}