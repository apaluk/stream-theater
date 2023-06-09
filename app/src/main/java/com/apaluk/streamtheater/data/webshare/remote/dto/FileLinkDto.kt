package com.apaluk.streamtheater.data.webshare.remote.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false, name = "response")
class FileLinkDto: ResponseDto() {
    @field:Element(name = "link", required = false)
    lateinit var link: String
}