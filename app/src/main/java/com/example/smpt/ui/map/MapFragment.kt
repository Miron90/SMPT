package com.example.smpt.ui.map

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.smpt.databinding.FragmentMapBinding
import com.example.smpt.ui.main.MainActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import org.osmdroid.config.Configuration.*



class MapFragment : Fragment(){

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        println(Configuration.getInstance().getOsmdroidBasePath(requireContext()))

        Configuration.getInstance().isDebugMapView = true
        Configuration.getInstance().isDebugMode = true
        Configuration.getInstance().isDebugTileProviders = true

        try {
            val inputStream: InputStream = requireContext().assets.open("bemowo.zip")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            val file = File(Configuration.getInstance().getOsmdroidBasePath(requireContext()), "bemowo.zip")
            FileOutputStream(file).use {
                it.write(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        binding.map.setUseDataConnection(false)
        binding.map.setTileSource(
            XYTileSource(
            "4uMaps",
            13,
            15,
            256,
            ".png",
            arrayOf(""))
        )
        binding.map.controller.setZoom(13.0)
        binding.map.minZoomLevel = 13.0
        binding.map.maxZoomLevel = 18.0
//        binding.map.projection.fromPixels(0, 0)
//        binding.map.projection.fromPixels(binding.map.width, binding.map.height)
        binding.map.controller.setCenter(GeoPoint(52.25, 20.95))
//        binding.map.boundingBox.set(
//            binding.map.projection.fromPixels(0, 0).latitude,
//            binding.map.projection.fromPixels(binding.map.width, binding.map.height).longitude,
//            binding.map.projection.fromPixels(binding.map.width, binding.map.height).latitude,
//            binding.map.projection.fromPixels(0, 0).longitude
//        )
//        println(binding.map.projection.fromPixels(0, 0).latitude)
//        println(binding.map.projection.fromPixels(binding.map.width, binding.map.height).longitude)

        return view
    }

    override fun onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        binding.map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        binding.map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                (activity as MainActivity),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
    }
}