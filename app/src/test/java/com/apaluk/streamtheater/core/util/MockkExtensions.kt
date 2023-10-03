package com.apaluk.streamtheater.core.util

import com.apaluk.streamtheater.core.resources.ResourcesManager
import io.mockk.every
import io.mockk.mockk

fun mockkResourcesManager(): ResourcesManager {
    val resourcesManager = mockk<ResourcesManager>()
    every { resourcesManager.getString(any()) } returns "some string"
    every { resourcesManager.getString(any(), any()) } returns "some string"
    every { resourcesManager.getColor(any()) } returns 1
    return resourcesManager
}