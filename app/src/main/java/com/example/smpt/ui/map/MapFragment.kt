package com.example.smpt.ui.map

import android.os.Bundle
import androidx.preference.PreferenceManager
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
import android.content.SharedPreferences
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.smpt.R
import com.example.smpt.R.drawable
import com.example.smpt.models.Sign
import com.example.smpt.ui.Constants
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.MapTileIndex

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.views.overlay.MapEventsOverlay
import java.lang.reflect.Field
import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG


class MapFragment : Fragment(), MapEventsReceiver {

    private var _binding: FragmentMapBinding? = null
    private lateinit var viewModel: MapViewModel
    private val binding get() = _binding!!
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    lateinit var currentLocation: GeoPoint
    lateinit var shapeLocation: GeoPoint
    private lateinit var tapLocation: GeoPoint

    var mapEventsOverlay = MapEventsOverlay(this)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this, MapViewModelFactory())
            .get(MapViewModel::class.java)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        //observer od lokalizacji uzytkownikow
        (activity as MainActivity).userLocations.observe(viewLifecycleOwner, {
            for (loc in it) {
                currentLocation = GeoPoint(loc.latitude, loc.longitude)
                if (loc.name.equals(sharedPreferences.getString(Constants().USERNAME, "noSharedPref")))
                    drawLocationMarker(R.color.green, loc.name!!)
                else
                    drawLocationMarker(R.color.teal_200, loc.name!!)
            }
        })

        (activity as MainActivity).shapeLocations.observe(viewLifecycleOwner, {
            var shapeId = 0
            val shape: MutableList<GeoPoint> = ArrayList<GeoPoint>()
            for (shapeLoc in it) {
                Log.d("Shape", shapeLoc.toString())
                if(shapeLoc.shapeId!! > shapeId){
                    Log.d("Shape", "in if" + shapeLoc.shapeId)
                    if(shape.size > 0) {
                        Log.d("Shape", "in if with size" + shape.size)
                        drawAShape(shape, shapeId.toString())
                    }
                    shapeId++;
                    shape.clear()
                }
                Log.d("Shape", "in added")
                shape.add(GeoPoint(shapeLoc.latitude, shapeLoc.longitude))
                shapeLocation = GeoPoint(shapeLoc.latitude, shapeLoc.longitude)
            }
            if(shape.size > 0) {
                Log.d("Shape", "in if with size $shapeId")
                drawAShape(shape, shapeId.toString())
            shape.clear()
            }
        })


        (activity as MainActivity).signsLocations.observe(viewLifecycleOwner, {
            for (sign in it) {
                Log.d("SIGNS", sign.toString())
                drawSignMarker(sign)
            }
        })

        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
//        Configuration.getInstance().isDebugMapView = true
//        Configuration.getInstance().isDebugMode = true
//        Configuration.getInstance().isDebugTileProviders = true

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

    private fun drawSignMarker(signTemp: Sign) {
        var signName = signTemp.signId.toString()+": "+signTemp.signCode
        Log.d("SIGNS", "in func")
        val svg: SVG = SVG.getFromString(signTemp.signSVG)
        val pd = PictureDrawable(svg.renderToPicture())
        val currentPosMarker = Marker(binding.map)
        binding.map.overlays.forEach {
            if (it is Marker && it.id == signName) {
                binding.map.overlays.remove(it)
            }
        }
        binding.map.invalidate()

        currentPosMarker.id = signName
        currentPosMarker.position = GeoPoint(signTemp.latitude,signTemp.longitude)

        currentPosMarker.icon = pd
        currentPosMarker.setAnchor(Marker.ANCHOR_TOP, Marker.ANCHOR_RIGHT)
        currentPosMarker.title = signName
        binding.map.overlays.add(currentPosMarker)
        Log.d("SIGNS", "in func")
        binding.map.invalidate()

    }

    override fun onResume() {
        super.onResume();
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause();
        binding.map.onPause()
    }

    private fun drawAShape(shape: MutableList<GeoPoint>, shapeId: String) {

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
        binding.map.overlayManager.add(polygon);
        binding.map.invalidate();
    }

    private fun drawLocationMarker(colorId: Int, name: String) {
        val currentPosMarker = Marker(binding.map)
        binding.map.overlays.forEach {
            if (it is Marker && it.id == name) {
                binding.map.overlays.remove(it)
            }
        }
        binding.map.invalidate()

        currentPosMarker.id = name
        currentPosMarker.position = currentLocation
        Log.d("position", name)
        setMarker(colorId, currentPosMarker, name)
    }

    private fun setMarker(colorId: Int, marker: Marker, name: String) {
        val icon = ContextCompat.getDrawable(requireContext(), drawable.ic_emoji_people)
        icon?.setTint(ContextCompat.getColor(requireContext(), colorId))
        marker.icon = icon
        marker.setAnchor(Marker.ANCHOR_TOP, Marker.ANCHOR_RIGHT)
        marker.title = name
        binding.map.overlays.add(marker)
        binding.map.invalidate()
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

    private fun setMapOverlays() {
        binding.map.controller.setZoom(13.0)
        binding.map.minZoomLevel = 13.0
        binding.map.maxZoomLevel = 18.0

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
        binding.map.overlays.add(0, mapEventsOverlay)
    }

    //funkcja od interfejsu MapRecievera (klik na mape)
    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        Toast.makeText(requireContext(), "Tapped", Toast.LENGTH_SHORT).show()
        return true
    }

    //funkcja od interfejsu MapRecievera (long klik na mape)
    override fun longPressHelper(p: GeoPoint?): Boolean {
        if (p != null) {
            tapLocation = GeoPoint(p)
            Toast.makeText(requireContext(), "Tap on (" + p.latitude + "," + p.longitude + ")",
                Toast.LENGTH_SHORT).show()
        }
        return false
    }
}