package com.apaluk.streamtheater.core.util

import com.apaluk.streamtheater.data.webshare.remote.dto.ResponseDto
import com.apaluk.streamtheater.data.webshare.remote.dto.StatusDto
import com.apaluk.streamtheater.data.webshare.remote.mapper.toStatusDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import org.simpleframework.xml.core.Persister
import retrofit2.Response
import timber.log.Timber

fun <D, R: ResponseDto> webShareRepositoryFlow(
    apiOperation: suspend () -> Response<ResponseBody>,
    dtoType: Class<out R>,
    resultMapping: suspend (R) -> D
): Flow<Resource<D>>  = flow {
    try {
        emit(Resource.Loading())
        val response = apiOperation.invoke()
        if (!response.isSuccessful) {
            emitError("HTTP error ${response.code()}")
            return@flow
        }
        val body = response.body()?.string() ?: run {
            emitError("Response body is null.")
            return@flow
        }
        val webshareResponse = Persister().read(dtoType, body)
        val webshareResponseStatus = webshareResponse.status.toStatusDto()
        if(webshareResponseStatus != StatusDto.OK) {
            emitError(webshareResponse.message)
            return@flow
        }
        val result = resultMapping(webshareResponse)
        emit(Resource.Success(data = result))
    } catch (e: Exception) {
        e.throwIfCancellation()
        emit(Resource.Error("Error: ${e.message}"))
        Timber.w(e)
    }
}

fun <D, R> repositoryFlow(
    apiOperation: suspend () -> Response<R>,
    resultMapping: suspend (R) -> D
): Flow<Resource<D>>  = flow {
    try {
        emit(Resource.Loading())
        val response = apiOperation.invoke()
        if (!response.isSuccessful) {
            emitError("Response: code=${response.code()} error=${response.errorBody()?.string()}")
            return@flow
        }
        val body = response.body() ?: run {
            emitError("Response body is null.")
            return@flow
        }
        val result = resultMapping(body)
        emit(Resource.Success(data = result))
    } catch (e: Exception) {
        e.throwIfCancellation()
        emit(Resource.Error("Error: ${e.message}"))
        Timber.w(e)
    }
}


inline fun <T, R : Any> Flow<Iterable<T>>.mapList(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    map { list -> list.map { transform(it) } }

private suspend fun <T> FlowCollector<Resource<T>>.emitError(message: String) {
    Timber.w(message)
    emit(Resource.Error(message = message))
}
