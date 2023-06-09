package com.apaluk.streamtheater.core.util.hashing

import com.google.common.hash.Hashing

fun String.md5Crypt(salt: String): String = Md5Crypt.md5Crypt(toByteArray(), "$1$$salt")

@Suppress("DEPRECATION")
fun String.sha1() = Hashing.sha1().hashString(this, Charsets.UTF_8).toString()
