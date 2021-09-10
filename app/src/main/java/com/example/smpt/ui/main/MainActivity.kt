package com.example.smpt.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smpt.BuildConfig
import com.example.smpt.R
import com.example.smpt.databinding.ActivityMainBinding
import com.example.smpt.remote.ApiInterface
import com.example.smpt.models.Localization
import com.example.smpt.ui.Constants
import com.example.smpt.models.ShapeLocalization
import com.example.smpt.models.Sign
import com.example.smpt.services.ForegroundOnlyLocationService
import com.google.android.gms.maps.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.osmdroid.util.GeoPoint

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE: Int = 34
    private lateinit var viewModel: MainViewModel

    private var foregroundLocationServiceBound = false
    private var foregroundLocationService: ForegroundOnlyLocationService? = null
    private lateinit var foregroundBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    var currentLocation = MutableLiveData<GeoPoint>()
    var userLocations = MutableLiveData<Array<Localization>>()
    var shapeLocations = MutableLiveData<Array<ShapeLocalization>>()
    var signsLocations = MutableLiveData<Array<Sign>>()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var shapeId: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MainViewModelFactory())
            .get(MainViewModel::class.java)

        foregroundBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        Log.d("Location", foregroundPermissionApproved().toString())
        if (foregroundPermissionApproved()) {
            Log.d("Location", foregroundPermissionApproved().toString())
            if (foregroundPermissionApproved()) {
                Log.d("Location", foregroundLocationService.toString())
            } else {
                Log.d("Location", "request")
                requestForegroundPermissions()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundLocationServiceBound) {
            unbindService(foregroundServiceConnection)
            foregroundLocationServiceBound = false
        }
        foregroundLocationService?.unsubscribeToLocationUpdates()
        super.onStop()
    }

    private val foregroundServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Location", "binder")
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundLocationService = binder.service
            foregroundLocationServiceBound = true
            if (foregroundPermissionApproved()) {
                Log.d("Location", foregroundLocationService.toString())
                foregroundLocationService?.subscribeToLocationUpdates()
                    ?: Log.d("Location", "Service not bound")
            } else {
                Log.d("Location", "request")
                requestForegroundPermissions()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Location", "not bound")
            foregroundLocationService = null
            foregroundLocationServiceBound = false
        }
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()
        if (provideRationale) {
            Snackbar.make(
                binding.root,
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            ).setAction("OK") {
                // Request permission
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                )
            }
                .show()
        } else {
            Log.d("Location", "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("Location", "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    Log.d("Location", "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    foregroundLocationService?.subscribeToLocationUpdates()
                else -> {
                    // Permission denied.
                    Snackbar.make(
                        binding.root,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings") {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude


                currentLocation.postValue(GeoPoint(location.latitude, location.longitude))
                // Log.d("Location", outputLocationText)
                Log.d("API", "sending data")

                val loc = Localization(latitude, longitude, sharedPreferences.getString(Constants().USERNAME, "noSharedPref"))
                val shapeLoc = ShapeLocalization(latitude, longitude, shapeId)

                val shapeInterface = ApiInterface.create().getShapeLocalization()
                shapeInterface.enqueue(object : Callback<Array<ShapeLocalization>> {
                    override fun onResponse(
                        call: Call<Array<ShapeLocalization>>,
                        response: Response<Array<ShapeLocalization>>
                    ) {
                        if (response.body() != null) {
                            shapeLocations.postValue(response.body()!!)
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
                        Log.d("API", "worksending" + response.message())
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
                            userLocations.postValue(response.body()!!)
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
//                        Log.d("API", "Error" + t.toString())
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
                            signsLocations.postValue(response.body()!!)
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
}
