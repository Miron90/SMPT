package com.example.smpt.ui.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smpt.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity(){
    private val viewModel: MainViewModel by viewModels()

    lateinit var client: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.showTargetsButton).setOnClickListener {
            Toast.makeText(this, "Troops has been shown", Toast.LENGTH_LONG).show()
        }
//        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
//
//        mapFragment.getMapAsync(this)
//
//        client = LocationServices.getFusedLocationProviderClient(this)
    }

//    override fun onMapReady(map: GoogleMap) {
//        if (checkLocationPermission()) {
//            client.lastLocation.addOnCompleteListener {
//                val latitude = it.result?.latitude
//                val longtitude = it.result?.longitude
//
//                val pos = LatLng(latitude!!, longtitude!!)
//
//                map.addMarker(MarkerOptions().position(pos).title("My pos"))
//
//                Toast.makeText(this, "My location: $latitude,  $longtitude", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    fun checkLocationPermission(): Boolean{
//        var state = false
//
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
//            if(this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                state = true
//            }else{
//                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION), 1000)
//            }
//        }else state = true
//
//        return state
//    }
}
