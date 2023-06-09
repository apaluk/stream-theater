package com.apaluk.streamtheater.data.webshare.remote.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false, name = "response")
class SaltDto: ResponseDto() {
    @field:Element(name = "salt", required = false)
    lateinit var salt: String
}

@Root(strict = false, name = "response")
class LoginDto: ResponseDto() {
    @field:Element(name = "token", required = false)
    lateinit var token: String
}
