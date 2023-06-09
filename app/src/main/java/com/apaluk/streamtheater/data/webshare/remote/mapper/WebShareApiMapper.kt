package com.apaluk.streamtheater.data.webshare.remote.mapper

import com.apaluk.streamtheater.data.webshare.remote.dto.StatusDto

fun String.toStatusDto() = when(this) {
    "OK" -> StatusDto.OK
    "FATAL" -> StatusDto.Fatal
    else -> StatusDto.Unknown
}