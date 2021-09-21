package com.example.smpt.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.models.Localization
import com.example.smpt.models.ShapeLocalization
import com.example.smpt.models.Sign
import com.example.smpt.remote.ApiInterface
import com.example.smpt.services.ForegroundOnlyLocationService
import com.example.smpt.ui.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForegroundOnlyBroadcastReceiver (private val api: ApiInterface, private val sharedPreferences: SharedPreferencesStorage) : BroadcastReceiver() {
    val removeMarkers = MutableLiveData<Boolean>()
    val shapeLocations = MutableLiveData<Array<ShapeLocalization>>()
    val userLocations = MutableLiveData<Array<Localization>>()
    val signsLocations = MutableLiveData<Array<Sign>>()
    override fun onReceive(context: Context, intent: Intent) {
        val location = intent.getParcelableExtra<Location>(
            ForegroundOnlyLocationService.EXTRA_LOCATION
        )
        removeMarkers.postValue(true);

        val apiInterface = api

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude



            val loc = Localization(latitude, longitude, sharedPreferences.getString(Constants().USERNAME))

            apiInterface.getShapeLocalization().enqueue(object : Callback<Array<ShapeLocalization>> {
                override fun onResponse(
                    call: Call<Array<ShapeLocalization>>,
                    response: Response<Array<ShapeLocalization>>
                ) {
                    if (response.body() != null) {
                        shapeLocations.postValue(response.body()!!)
                        for (shapeLoc in response.body()!!) {
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Array<ShapeLocalization>>?,
                    t: Throwable?
                ) {
                }
            })

            apiInterface.sendLocalization(loc).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                }
            })

            apiInterface.getLocalization().enqueue(object : Callback<Array<Localization>> {
                override fun onResponse(
                    call: Call<Array<Localization>>,
                    response: Response<Array<Localization>>
                ) {
                    if (response.body() != null) {
                        userLocations.postValue(response.body()!!)
                    }
                }

                override fun onFailure(
                    call: Call<Array<Localization>>?,
                    t: Throwable?
                ) {
                }
            })

            apiInterface.getSigns().enqueue(object : Callback<Array<Sign>> {
                override fun onResponse(
                    call: Call<Array<Sign>>,
                    response: Response<Array<Sign>>
                ) {
                    if (response.body() != null) {
                        signsLocations.postValue(response.body()!!)
                    }
                }

                override fun onFailure(
                    call: Call<Array<Sign>>?,
                    t: Throwable?
                ) {
                }
            })
        }
    }
}