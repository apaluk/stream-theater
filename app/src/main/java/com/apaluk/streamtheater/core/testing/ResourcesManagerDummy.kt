package com.apaluk.streamtheater.core.testing

import android.annotation.SuppressLint
import com.apaluk.streamtheater.core.resources.ResourcesManager

class ResourcesManagerDummy : ResourcesManager {
    @SuppressLint("ResourceAsColor")
    override fun getColor(colorRes: Int): Int = colorRes

    override fun getString(stringRes: Int): String = stringRes.toString()

    override fun getString(stringRes: Int, vararg arguments: Any): String = stringRes.toString()
}