package com.example.smpt.ui.main

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.example.smpt.R
import com.example.smpt.databinding.FragmentTestBinding
import androidx.core.app.ActivityCompat.requestPermissions

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentTest : Fragment() {


    private var _binding: FragmentTestBinding? = null

    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as MainActivity)

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}