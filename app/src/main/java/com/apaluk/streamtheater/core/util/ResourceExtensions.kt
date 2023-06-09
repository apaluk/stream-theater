package com.apaluk.streamtheater.core.util

fun <T, R> Resource<T>.convertNonSuccess(): Resource<R> {
    require(this !is Resource.Success)
    return if(this is Resource.Error)
        Resource.Error(message, exception, null)
    else
        Resource.Loading()
}
