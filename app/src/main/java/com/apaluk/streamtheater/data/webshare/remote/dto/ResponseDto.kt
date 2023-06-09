package com.apaluk.streamtheater.data.webshare.remote.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false, name = "response")
open class ResponseDto {
    @field:Element(name = "status", required = true)
    lateinit var status: String
    @field:Element(name = "message", required = false)
    lateinit var message: String
}

enum class StatusDto {
    OK, Fatal, Unknown
}
