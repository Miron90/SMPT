package com.example.smpt.remote

import com.example.smpt.ui.Constants
import com.example.smpt.models.Localization
import com.example.smpt.models.ShapeLocalization
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.google.gson.GsonBuilder

interface ApiInterface {

    @GET("api/users")
    fun getLocalization() : Call<Array<Localization>>

    @POST("api/users")
    fun sendLocalization(@Body localization: Localization): Call<String>

    @GET("api/zone")
    fun getShapeLocalization() : Call<Array<ShapeLocalization>>

    @POST("api/zone")
    fun sendShapeLocalization(@Body shapeLocalization: ShapeLocalization): Call<String>

    companion object {

        fun create() : ApiInterface {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants().BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }
}