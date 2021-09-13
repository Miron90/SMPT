package com.example.smpt.remote

import com.example.smpt.ui.Constants
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
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