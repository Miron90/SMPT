package com.example.smpt.ui

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.google.gson.GsonBuilder

import com.google.gson.Gson




interface ApiInterface {

    @GET("api/map")
    fun getLocalization() : Call<Array<Localization>>


    @POST("api/map")
    fun sendLocalization(@Body localization: Localization): Call<String>

    companion object {

        var BASE_URL = "http://172.16.35.152:5001"

        fun create() : ApiInterface {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }

}