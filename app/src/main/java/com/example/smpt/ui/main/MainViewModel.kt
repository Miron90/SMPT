package com.example.smpt.ui.main

import android.util.Log
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.models.Sign
import com.example.smpt.remote.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    val apiInterface: ApiInterface,
    val sharedPreferencesStorage: SharedPreferencesStorage
){

    fun downloadSigns() {
        var signs: Array<Sign>? = null
        apiInterface.getSignOrderBy().enqueue(object : Callback<Array<Sign>> {
            override fun onResponse(
                call: Call<Array<Sign>>,
                response: Response<Array<Sign>>
            ) {
                if (response.body() != null) {
                    Log.d(
                        "API",
                        "send sign" + response.message()
                    )

                    signs = response.body()!!
                    if (signs!!.isNotEmpty())
                        sharedPreferencesStorage.setSigns(signs!!)
                }
                Log.d("API", "send sign" + response.message())
            }

            override fun onFailure(call: Call<Array<Sign>>?, t: Throwable?) {
                Log.d("API", "Error" + t.toString())
            }
        })
    }
}