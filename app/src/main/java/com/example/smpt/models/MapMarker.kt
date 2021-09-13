package com.example.smpt.models

import org.osmdroid.util.GeoPoint

class MapMarker(geoPoint: GeoPoint?, fromServer: Boolean) {
    var point = geoPoint
    var delete = fromServer

}