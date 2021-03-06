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
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.smpt.R.drawable
import com.example.smpt.ui.Constants
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.MapTileIndex
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.views.overlay.MapEventsOverlay
import android.graphics.drawable.PictureDrawable
import android.widget.*
import com.caverock.androidsvg.SVG
import com.example.smpt.R
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.models.MapMarker
import com.example.smpt.receivers.ForegroundOnlyBroadcastReceiver
import com.example.smpt.remote.ApiInterface
import com.example.smpt.ui.dialogs.DialogSign
import com.example.smpt.ui.settings.SettingsFragment
import com.google.gson.Gson
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.osmdroid.views.CustomZoomButtonsController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), MapEventsReceiver {

    private var _binding: FragmentMapBinding? = null
    private lateinit var viewModel: MapViewModel
    private val binding get() = _binding!!
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private var mapMarkers: MutableMap<String, MapMarker> = HashMap()
    lateinit var shapeLocation: GeoPoint
    var shapePoints: ArrayList<GeoPoint> = ArrayList()
    private lateinit var tapLocation: GeoPoint
    private val foregroundBroadcastReceiver: ForegroundOnlyBroadcastReceiver by inject()
    private lateinit var linearLayout:LinearLayout
    private lateinit var textView:TextView
    var mapEventsOverlay = MapEventsOverlay(this)
    private val sharedPreferences: SharedPreferencesStorage by inject()
    private val apiInterface: ApiInterface by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = get()


        sharedPreferences.otherMarkerColor.observe(viewLifecycleOwner, {
            removeAllMarkers()
        })

        sharedPreferences.ownMarkerColor.observe(viewLifecycleOwner, {
            removeAllMarkers()
        })

        foregroundBroadcastReceiver.removeMarkers.observe(viewLifecycleOwner, {
            removeMarkers()
        })
        //observer od lokalizacji uzytkownikow
        foregroundBroadcastReceiver.userLocations.observe(viewLifecycleOwner, {
            for (loc in it) {
                //mapMarkers[loc.name!!] = MapMarker(GeoPoint(loc.latitude, loc.longitude),true)
                if (loc.name.equals(sharedPreferences.getString(Constants().USERNAME)))
                    draw(
                        loc.name!!,
                        GeoPoint(loc.latitude, loc.longitude),
                        null,
                        null,
                        sharedPreferences.getOwnMarkerColor()
                    )
                else
                    draw(
                        loc.name!!,
                        GeoPoint(loc.latitude, loc.longitude),
                        null,
                        null,
                        sharedPreferences.getOtherMarkerColor()
                    )
            }
        })

        foregroundBroadcastReceiver.shapeLocations.observe(viewLifecycleOwner, {
            var shapeId:Int = -1
            if(it.size>0) {
                shapeId = it.get(0).shapeId!!
            }
            val shape: MutableList<GeoPoint> = ArrayList<GeoPoint>()
            for (shapeLoc in it) {
                if (shapeLoc.shapeId!! > shapeId!!) {
                    if (shape.size > 0) {
                        draw(
                            shape[0].latitude.toString() + " " + shape[shape.size - 1].latitude,
                            null,
                            null,
                            shape,
                            0
                        )
                    }
                    shapeId++
                    shape.clear()

                }
                shape.add(GeoPoint(shapeLoc.latitude, shapeLoc.longitude))
                shapeLocation = GeoPoint(shapeLoc.latitude, shapeLoc.longitude)
            }
            if (shape.size > 0) {
                draw(
                    shape[0].latitude.toString() + " " + shape[shape.size - 1].latitude,
                    null,
                    null,
                    shape,
                    0
                )
                shape.clear()
            }
        })


        foregroundBroadcastReceiver.signsLocations.observe(viewLifecycleOwner, {
            for (sign in it) {
                Log.d("SIGNS", sign.toString())
                draw(
                    sign.signId.toString() + ": " + sign.signCode,
                    GeoPoint(sign.latitude, sign.longitude), sign.signSVG, null, 0
                )
            }
        })

        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))


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


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settings.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, SettingsFragment()).addToBackStack("mapFragment").commit()
        }
        binding.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        linearLayout = (activity as MainActivity).findViewById(R.id.zonesLayout) as LinearLayout
        textView = (linearLayout.findViewById(R.id.geoPoints) as TextView)
        binding.zones.setOnClickListener {
            if(linearLayout.visibility == View.GONE){
                linearLayout.visibility = View.VISIBLE
                textView.text = shapePoints.size.toString()
            }else {
                linearLayout.visibility = View.GONE
                shapePoints.clear()
                binding.map.overlays.forEach {
                    if((it is Polygon) && it.id.equals("temp")){
                        binding.map.overlays.remove(it)
                    }
                }
            }
        }
        (linearLayout.findViewById(R.id.addZones) as Button).setOnClickListener{
        Log.d("beczka spr",shapePoints.size.toString())
            if(shapePoints.size>1) {
                apiInterface.sendShape(shapePoints).enqueue(object :
                    Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                    }

                    override fun onFailure(
                        call: Call<String>?,
                        t: Throwable?
                    ) {
                    }
                })
                shapePoints.clear()
                linearLayout.visibility = View.GONE
                textView.text = shapePoints.size.toString()
                binding.map.overlays.forEach {
                    if ((it is Polygon) && it.id.equals("temp")) {
                        binding.map.overlays.remove(it)
                    }
                }
            }
        }
        (linearLayout.findViewById(R.id.revert) as ImageButton).setOnClickListener{

            if(shapePoints.size>0) {
                shapePoints.removeAt(shapePoints.size-1)
                textView.text = shapePoints.size.toString()
                if(shapePoints.size>1) {
                    drawPolygon(shapePoints)
                }else{
                    binding.map.overlays.forEach {
                        if ((it is Polygon) && it.id.equals("temp")) {
                            binding.map.overlays.remove(it)
                        }
                    }
                }
            }else if (shapePoints.size == 0){
                Toast.makeText(requireContext(), "can do that", Toast.LENGTH_SHORT).show()
                textView.text = shapePoints.size.toString()
            }
        }
        setMapOverlays()
    }

    private fun removeMarkers() {
        binding.map.overlays.forEach {
            if (it is Marker && (mapMarkers.containsKey(it.id) && mapMarkers[it.id]?.point?.equals(
                    it.position
                ) == false)
            ) {
                binding.map.overlays.remove(it)
                mapMarkers.remove(it.id)
            } else if (it is Polygon && it.id.equals("temp")){
                //do nothing
            } else if (it is Polygon && mapMarkers.containsKey(it.id) && mapMarkers[it.id]?.delete == false) {
                binding.map.overlays.remove(it)
                mapMarkers.remove(it.id)
            } else if ((it is Marker && mapMarkers[it.id]?.delete == false)) {
                binding.map.overlays.remove(it)
                mapMarkers.remove(it.id)
            } else if ((it is Polygon && mapMarkers[it.id]?.delete == false)) {
                binding.map.overlays.remove(it)
                mapMarkers.remove(it.id)
            } else if (it is Polygon) {
                mapMarkers[it.id] = MapMarker(mapMarkers[it.id]?.point, false)
            } else if (it is Marker) {
                mapMarkers[it.id] = MapMarker(mapMarkers[it.id]?.point, false)
            }
        }
    }

    fun removeAllMarkers() {
        binding.map.overlays.forEach {
            if (it is Marker) {
                binding.map.overlays.remove(it)
                mapMarkers.remove(it.id)
            }
        }
    }


    private fun draw(
        name: String,
        position: GeoPoint?,
        signSvg: String?,
        polygonArray: MutableList<GeoPoint>?,
        colorId: Int
    ) {

        if (mapMarkers.containsKey(name)) {
            mapMarkers[name] = MapMarker(position, true)
            return
        } else {
            mapMarkers[name] = MapMarker(position, true)
            if (polygonArray != null) {
                Log.d("works", polygonArray.toString())
                val polygon = Polygon()
                polygonArray.add(polygonArray[0]) //forces the loop to close(connect last point to first point)
                polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color
                polygon.points = polygonArray
                polygon.id = name
                polygon.title="xdd"
                binding.map.overlayManager.add(polygon)
                binding.map.invalidate()
            } else {
                var icon: Drawable? =
                    ContextCompat.getDrawable(requireContext(), drawable.ic_emoji_people)
                if (signSvg != null) {
                    val svg = SVG.getFromString(signSvg)
                    svg.documentHeight = sharedPreferences.signSize.toFloat()
                    svg.documentWidth = sharedPreferences.signSize.toFloat()
                    icon = PictureDrawable(svg.renderToPicture())
                }
                val currentPosMarker = Marker(binding.map)
                currentPosMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                currentPosMarker.id = name
                currentPosMarker.position = position
                Log.d("position", name)
                if (colorId != 0) {
                    icon?.setTint(ContextCompat.getColor(requireContext(), colorId))
                }
                currentPosMarker.icon = icon
                currentPosMarker.title = name
                binding.map.overlays.add(currentPosMarker)
                binding.map.invalidate()
            }
        }
    }

    override fun onResume() {
        super.onResume();
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause();
        binding.map.onPause()
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
        if(linearLayout.visibility == View.VISIBLE){
            if (p != null) {
                shapePoints.add(p)
                if(shapePoints.size>1) {
                    drawPolygon(shapePoints)
                }
            }
        }

        return true
    }

    private fun drawPolygon(shapePoints: ArrayList<GeoPoint>) {
        textView.text = shapePoints.size.toString()
        binding.map.overlays.forEach {
            if((it is Polygon) && it.id.equals("temp")){
                binding.map.overlays.remove(it)
            }
        }
        val polygon = Polygon()
        val points:ArrayList<GeoPoint> = shapePoints.clone() as ArrayList<GeoPoint>
        points.add(points[0]) //forces the loop to close(connect last point to first point)
        polygon.fillPaint.color = Color.parseColor("#9696960E") //set fill color
        polygon.points = shapePoints
        polygon.id = "temp"
        binding.map.overlayManager.add(polygon)
        binding.map.invalidate()
    }

    //funkcja od interfejsu MapRecievera (long klik na mape)
    override fun longPressHelper(p: GeoPoint?): Boolean {
        if (p != null) {
            tapLocation = GeoPoint(p)
            DialogSign(requireContext(), tapLocation, apiInterface, sharedPreferences)
        }
        return false
    }
}