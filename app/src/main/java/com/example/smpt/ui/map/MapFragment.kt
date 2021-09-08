package com.example.smpt.ui.map

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.Polygon
import android.R
import android.content.SharedPreferences
import android.graphics.Color
import com.example.smpt.ui.Constants


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    lateinit var currentLocation: GeoPoint
    lateinit var tapLocation: GeoPoint

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        //observer od m
        (activity as MainActivity).userLocations.observe(viewLifecycleOwner, {
            for (loc in it) {
                currentLocation = GeoPoint(loc.latitude, loc.longtitude)
                if (loc.name.equals(
                        sharedPreferences.getString(
                            Constants().USERNAME,
                            "noSharedPref"
                        )
                    )
                ) {
                    drawLocationMarker("Green", loc.name!!)
                } else {
                    drawLocationMarker("Blue", loc.name!!)
                }

            }
        })

        //observer od markera klikniecia (LiveData)
        (activity as MainActivity).tapLocation.observe(viewLifecycleOwner, {
            tapLocation = it
            //wywolywanie testowej funkcji rysowania
            drawPolylineTest()
        })

        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        Configuration.getInstance().isDebugMapView = true
        Configuration.getInstance().isDebugMode = true
        Configuration.getInstance().isDebugTileProviders = true

        prepareMapFile("bemowo.zip")

        binding.map.setUseDataConnection(false)
        binding.map.setTileSource(
            XYTileSource(
                "4uMaps",
                13,
                15,
                256,
                ".png",
                arrayOf("")
            )
        )
        setMapOverlays()
        return view
    }

    private fun drawLocationMarker(s: String, name: String) {
        val currentPosMarker = Marker(binding.map)
        binding.map.overlays.forEach {
            if (it is Marker && it.id == name) {
                binding.map.overlays.remove(it)
            }
        }
        binding.map.invalidate();

        currentPosMarker.id = name
        currentPosMarker.position = currentLocation
        Log.d("position", name)
        if (s.equals("Green")) {

            currentPosMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            currentPosMarker.title = name;
            binding.map.overlays.add(currentPosMarker)
            binding.map.invalidate();

        } else if (s.equals("Blue")) {

            //ikonka do zmiany
            currentPosMarker.setIcon(getResources().getDrawable(R.drawable.btn_plus));
            currentPosMarker.setAnchor(Marker.ANCHOR_TOP, Marker.ANCHOR_RIGHT)
            currentPosMarker.title = name
            binding.map.overlays.add(currentPosMarker)
            binding.map.invalidate();
        }
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

    //Dodawanie rysunku (fun testowa)
    private fun drawPolylineTest() {
        val circle = Polygon(binding.map)
        circle.points = Polygon.pointsAsCircle(tapLocation, 1000.0)
        circle.setFillColor(0x12121212);
        circle.setStrokeColor(Color.RED);
        circle.setStrokeWidth(2F);
        circle.title =
            ("Center of circle x: " + tapLocation.latitude + " y: " + tapLocation.longitude)
        binding.map.overlays.add(circle);
        binding.map.invalidate();
    }

    private fun setMapOverlays() {
        binding.map.controller.setZoom(13.0)
        binding.map.minZoomLevel = 13.0
        binding.map.maxZoomLevel = 18.0

        //ROTACJA MAPY
        //val rotationGestureOverlay = RotationGestureOverlay(binding.map);
        //rotationGestureOverlay.isEnabled
        //binding.map.overlays.add(rotationGestureOverlay);

        binding.map.setMultiTouchControls(true);

        //PUNKT WIDOKU MAPY
        binding.map.controller.setCenter(GeoPoint(52.25, 20.95))

        //KOMPAS
        val compassOverlay = CompassOverlay(
            requireContext(),
            InternalCompassOrientationProvider(requireContext()),
            binding.map
        )
        compassOverlay.enableCompass()
        binding.map.overlays.add(compassOverlay)
        binding.map.setScrollableAreaLimitDouble(
            BoundingBox(
                52.3,
                21.05,
                52.2,
                20.825
            )
        )
        binding.map.overlays.add(0, (activity as MainActivity).mapEventsOverlay)
    }

    private fun prepareMapFile(fileName: String) {
        try {
            val inputStream: InputStream = requireContext().assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            val file =
                File(Configuration.getInstance().getOsmdroidBasePath(requireContext()), fileName)
            FileOutputStream(file).use {
                it.write(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}