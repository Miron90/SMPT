package com.example.smpt.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.smpt.models.Localization
import com.example.smpt.models.ShapeLocalization
import com.example.smpt.models.Sign
import com.example.smpt.remote.ApiInterface
import com.example.smpt.services.ForegroundOnlyLocationService
import com.example.smpt.ui.Constants
import com.example.smpt.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForegroundOnlyBroadcastReceiver (val main: MainActivity) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val location = intent.getParcelableExtra<Location>(
            ForegroundOnlyLocationService.EXTRA_LOCATION
        )

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude


            Log.d("API", "sending data")

            val loc = Localization(latitude, longitude, PreferenceManager.getDefaultSharedPreferences(context).getString(Constants().USERNAME, "noSharedPref"))

            val shapeInterface = ApiInterface.create().getShapeLocalization()
            shapeInterface.enqueue(object : Callback<Array<ShapeLocalization>> {
                override fun onResponse(
                    call: Call<Array<ShapeLocalization>>,
                    response: Response<Array<ShapeLocalization>>
                ) {
                    if (response.body() != null) {
                        main.shapeLocations.postValue(response.body()!!)
                        for (shapeLoc in response.body()!!) {
                            Log.d("API", "shape work$shapeLoc")
                            //sharedPreferences.getString(Constants().USERNAME, "noSharedPref")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Array<ShapeLocalization>>?,
                    t: Throwable?
                ) {
                    Log.d("API", "shape Error" + t.toString())
                }
            })


            val apiInterfacesend = ApiInterface.create().sendLocalization(loc)
            apiInterfacesend.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.body() != null) Log.d(
                        "API",
                        "work" + response.message()
                    )
                    Log.d("API", "work" + response.message())
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.d("API", "Error" + t.toString())
                }
            })
            val apiInterface = ApiInterface.create().getLocalization()
            apiInterface.enqueue(object : Callback<Array<Localization>> {
                override fun onResponse(
                    call: Call<Array<Localization>>,
                    response: Response<Array<Localization>>
                ) {
                    if (response.body() != null) {
                        main.userLocations.postValue(response.body()!!)
                        for (loc in response.body()!!) {
                            Log.d("API", "work" + loc)
                            //sharedPreferences.getString(Constants().USERNAME, "noSharedPref")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Array<Localization>>?,
                    t: Throwable?
                ) {
                    Log.d("API", "Error" + t.toString())
                }
            })
            Log.d("API", apiInterface.toString())


            val signInterface = ApiInterface.create().getSigns()
            signInterface.enqueue(object : Callback<Array<Sign>> {
                override fun onResponse(
                    call: Call<Array<Sign>>,
                    response: Response<Array<Sign>>
                ) {
                    if (response.body() != null) {
                        main.signsLocations.postValue(response.body()!!)
                        for (loc in response.body()!!) {
                            Log.d("API", "work" + loc)
                            //sharedPreferences.getString(Constants().USERNAME, "noSharedPref")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Array<Sign>>?,
                    t: Throwable?
                ) {
                    Log.d("API", "Error" + t.toString())
                }
            })
            Log.d("API", apiInterface.toString())
        }
    }
}