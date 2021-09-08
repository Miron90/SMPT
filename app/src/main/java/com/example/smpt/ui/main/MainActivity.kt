package com.example.smpt.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.telecom.TelecomManager.EXTRA_LOCATION
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smpt.BuildConfig
import com.example.smpt.R
import com.example.smpt.ui.ApiInterface
import com.example.smpt.ui.Localization
import com.example.smpt.databinding.ActivitySecondBinding
import com.example.smpt.databinding.FragmentMapBinding
import com.example.smpt.ui.map.MapFragment
import com.example.smpt.ui.Constants
import com.example.smpt.ui.services.ForegroundOnlyLocationService
import com.google.android.gms.maps.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Polygon

class MainActivity : AppCompatActivity(), MapEventsReceiver {
    private lateinit var sharedPreferences: SharedPreferences
    private val binding: ActivitySecondBinding by lazy {
        ActivitySecondBinding.inflate(layoutInflater)
    }
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE: Int = 34
    private val viewModel: MainViewModel by viewModels()

    private var foregroundLocationServiceBound = false

    private var foregroundLocationService: ForegroundOnlyLocationService? = null

    private lateinit var foregroundBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    var currentLocation = MutableLiveData<GeoPoint>()
    var tapLocation = MutableLiveData<GeoPoint>()


    var userLocations = MutableLiveData<Array<Localization>>()
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    var mapEventsOverlay = MapEventsOverlay(this, this)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foregroundBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

            Log.d("Location", foregroundPermissionApproved().toString())
            if (foregroundPermissionApproved()) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)



                Log.d("Location", foregroundPermissionApproved().toString())
                if (foregroundPermissionApproved()) {
                    Log.d("Location", foregroundLocationService.toString())
//                    foregroundLocationService?.subscribeToLocationUpdates()
//                        ?: Log.d("Location", "Service not bound")
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

            private fun foregroundPermissionApproved(): Boolean {
                return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

            private fun requestForegroundPermissions() {
                val provideRationale = foregroundPermissionApproved()

                // If the user denied a previous request, but didn't check "Don't ask again", provide
                // additional rationale.
                if (provideRationale) {
                    Snackbar.make(
                        findViewById(R.id.activity_main),
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
                            // If user interaction was interrupted, the permission request
                            // is cancelled and you receive empty arrays.
                            Log.d("Location", "User interaction was cancelled.")
                        grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                            // Permission was granted.
                            foregroundLocationService?.subscribeToLocationUpdates()
                        else -> {
                            // Permission denied.

                            Snackbar.make(
                                findViewById(R.id.activity_main),
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
            //funkcja od interfejsu MapRecievera
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show()
                //tapLocation.postValue(GeoPoint(p))
                return true
            }

            //funkcja od interfejsu MapRecievera
            override fun longPressHelper(p: GeoPoint?): Boolean {
                if (p != null) {

                    tapLocation.postValue(GeoPoint(p))
                    Toast.makeText(
                        this,
                        "Tap on (" + p.latitude + "," + p.longitude + ")",
                        Toast.LENGTH_SHORT
                    ).show()
                };
                return false
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
                        // Create JSON using JSONObject
                        var loc = Localization(
                            latitude,
                            longitude,
                            sharedPreferences.getString(Constants().USERNAME, "noSharedPref")
                        )


                        val apiInterfacesend = ApiInterface.create().sendLocalization(loc)
                        apiInterfacesend.enqueue(object : Callback<String> {
                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                if (response?.body() != null) Log.d(
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
                                if (response?.body() != null) {
                                    userLocations.postValue(response.body()!!)
                                    for (loc in response.body()!!) {
                                        Log.d("API", "work" + loc)
                                        //TODO zrob cos z punktami
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
                        // Log.d("Location", outputLocationText)
                    }
                }
            }

            fun Location?.toText(): String {
                return if (this != null) {
                    "($latitude, $longitude)"
                } else {
                    "Unknown location"
                }
            }


        }




