package com.apaluk.streamtheater.core.util

fun <T, R> Resource<T>.convertNonSuccess(): Resource<R> {
    check(this !is Resource.Success)
    return if(this is Resource.Error)
        Resource.Error(message, exception, null)
    else
        Resource.Loading()
}
