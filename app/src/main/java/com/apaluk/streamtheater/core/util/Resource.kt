package com.apaluk.streamtheater.core.util


/** A generic class that contains data and status about loading this data. */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val exception: Exception? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String? = null, exception: Exception? = null, data: T? = null) :
        Resource<T>(data, message, exception)
}