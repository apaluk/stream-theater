package com.apaluk.streamtheater.data.webshare.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WebShareApi {

    @FormUrlEncoded
    @POST("/api/salt/")
    @Headers(
        "Accept: text/xml; charset=UTF-8",
        "Content-Type: application/x-www-form-urlencoded; charset=UTF-8"
    )
    suspend fun salt(
        @Field("username_or_email") username: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/api/login/")
    @Headers(
        "Accept: text/xml; charset=UTF-8",
        "Content-Type: application/x-www-form-urlencoded; charset=UTF-8"
    )
    suspend fun login(
        @Field("username_or_email") userName: String,
        @Field("password") password: String,
        @Field("keep_logged_in") keepLoggedIn: Int = 1
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("/api/file_link/")
    @Headers(
        "Accept: text/xml; charset=UTF-8",
        "Content-Type: application/x-www-form-urlencoded; charset=UTF-8"
    )
    suspend fun getFileLink(
        @Field("wst") token: String,
        @Field("ident") fileIdent: String,
        @Field("device_uuid") deviceId: String = "a58bfeb9-7c2b-4e19-9d24-5e3173a42376",
        @Field("download_type") downloadType: String = "video_stream",
        @Field("device_vendor") deviceVendor: String = "iscc",


    ): Response<ResponseBody>


}