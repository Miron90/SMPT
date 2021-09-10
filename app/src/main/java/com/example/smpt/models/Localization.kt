package com.example.smpt.models


data class Localization(var latitude: Double, var longitude: Double, var name: String?)

data class ShapeLocalization(var latitude: Double, var longitude: Double, var shapeId: Int?)

data class Sign(var latitude: Double, var longitude: Double, var signSVG: String, var signId: Int, var signCode: String)
