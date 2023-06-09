package com.apaluk.streamtheater.core.util

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Base64

fun String.urlEncode(): String = URLEncoder.encode(this, "utf-8")
fun String.urlDecode(): String = URLDecoder.decode(this, "utf-8")

fun String.base64Encode(): String = Base64.getEncoder().encodeToString(toByteArray())
fun String.base64Decode(): String = String(Base64.getDecoder().decode(this))
