package com.example.smpt.ui

data class Localizationx (var value: Array<String>)

data class Localization(var latitude: Double, var longitude: Double, var name: String?)

data class ShapeLocalization(var latitude: Double, var longitude: Double, var shapeId: Int?)

data class LocalizationResponse (var message: String)
