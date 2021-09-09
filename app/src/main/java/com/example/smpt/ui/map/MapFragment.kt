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
import org.osmdroid.util.GeoPoint
import java.util.ArrayList
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.Polygon
import android.R
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.example.smpt.ui.Constants
import org.osmdroid.util.MapTileIndex

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    lateinit var currentLocation: GeoPoint
    lateinit var shapeLocation: GeoPoint
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

        //observer od lokalizacji uzytkownikow
        (activity as MainActivity).userLocations.observe(viewLifecycleOwner, {
            for (loc in it) {
                currentLocation = GeoPoint(loc.latitude, loc.longitude)
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

        (activity as MainActivity).shapeLocations.observe(viewLifecycleOwner, {
            var shapeId: Int = 0
            var shape: MutableList<GeoPoint> = ArrayList<GeoPoint>()
            for (shapeLoc in it) {
                Log.d("Shape", shapeLoc.toString());
                if(shapeLoc.shapeId!! > shapeId){
                    Log.d("Shape", "in if"+shapeLoc.shapeId);
                    if(shape.size>0) {
                        Log.d("Shape", "in if with size"+shape.size);
                        drawAShape(shape, shapeId.toString())
                    }
                    shapeId++;
                    shape.clear()
                }
                Log.d("Shape", "in added");
                shape.add(GeoPoint(shapeLoc.latitude, shapeLoc.longitude))
                shapeLocation = GeoPoint(shapeLoc.latitude, shapeLoc.longitude)
            }
            if(shape.size>0) {
                Log.d("Shape", "in if with size"+shapeId);
                drawAShape(shape, shapeId.toString())
            shape.clear()// <3 shapeId is a id of the shape
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

        binding.map.setTileSource(object : OnlineTileSourceBase(
            "",
            10,
            15,
            256,
            "",
            arrayOf(Constants().TILE_URL)
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return (baseUrl
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding)
            }
        })

        setMapOverlays()
        return view
    }

    private fun drawAShape(shape: MutableList<GeoPoint>, shapeId: String) {
        val geoPoints: MutableList<GeoPoint> = ArrayList()
//add your points here
//add your points here
        val polygon = Polygon() //see note below

        shape.add(shape[0]) //forces the loop to close(connect last point to first point)

        polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color

        polygon.points = shape
        polygon.title = "A sample polygon"
        polygon.id = shapeId
        binding.map.overlays.forEach {
            if (it is Polygon && it.id == shapeId) {
                binding.map.overlays.remove(it)
            }
        }
        binding.map.getOverlayManager().add(polygon);

        binding.map.invalidate();
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
//        val circle = Polygon(binding.map)
//        circle.points = Polygon.pointsAsCircle(tapLocation, 1000.0)
//        circle.setFillColor(0x12121212);
//        circle.setStrokeColor(Color.RED);
//        circle.setStrokeWidth(2F);
//        circle.title =
//            ("Center of circle x: " + tapLocation.latitude + " y: " + tapLocation.longitude)
//        binding.map.overlays.add(circle);
//        binding.map.invalidate();

        val geoPoints: MutableList<GeoPoint> = ArrayList()
        geoPoints.add(GeoPoint(52.2448,20.9591))
        geoPoints.add(GeoPoint(52.2082,21.0051))
        geoPoints.add(GeoPoint(52.2094,20.9560))
        geoPoints.add(GeoPoint(52.2305,21.0034))
        val polygon = Polygon() //see note below
        geoPoints.add(geoPoints[0]) //forces the loop to close(connect last point to first point)
        polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color
        polygon.points = geoPoints
        polygon.title = "A sample polygon"
        binding.map.overlays.add(polygon);
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
//        binding.map.setScrollableAreaLimitDouble(
//            BoundingBox(
//                52.3,
//                21.05,
//                52.2,
//                20.825
//            )
//        )
        binding.map.overlays.add(0, (activity as MainActivity).mapEventsOverlay)
    }
}