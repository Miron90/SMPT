package com.example.smpt

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.example.smpt.models.Sign

class SharedPreferencesStorage (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)

    private lateinit var signs: Array<Sign>
    private var certPassword = ""
    private var ownMarkerColor = R.color.green
    private var otherMarkerColor = R.color.blue

    fun setOtherOwnMarkerColor(colorId: Int) {
        otherMarkerColor = colorId
    }

    fun getOtherMarkerColor(): Int {
        return otherMarkerColor
    }

    fun setOwnMarkerColor(colorId: Int) {
        ownMarkerColor = colorId
    }

    fun getOwnMarkerColor(): Int {
        return ownMarkerColor
    }

    fun setCertPassword(password: String) {
        certPassword = password
    }

    fun getCertPassword(): String {
        return certPassword
    }

    fun setSigns(signsArray: Array<Sign>) {
        signs = signsArray
    }

    fun getSigns(): Array<Sign> {
        return signs
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "").toString()
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}