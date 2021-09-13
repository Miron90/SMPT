package com.example.smpt.remote

import com.example.smpt.models.Localization
import com.example.smpt.models.ShapeLocalization
import com.example.smpt.models.Sign
import com.example.smpt.models.SignUploadDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET("api/users")
    fun getLocalization() : Call<Array<Localization>>

    @POST("api/users")
    fun sendLocalization(@Body localization: Localization): Call<String>

    @GET("api/zone")
    fun getShapeLocalization() : Call<Array<ShapeLocalization>>

    @GET("api/signs")
    fun getSigns() : Call<Array<Sign>>

    @POST("api/signs")
    fun sendSign(@Body signUploadDto: SignUploadDto): Call<String>
}