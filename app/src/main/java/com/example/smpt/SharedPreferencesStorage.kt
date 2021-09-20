package com.example.smpt

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.MutableLiveData
import com.example.smpt.models.Sign

class SharedPreferencesStorage (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)

    private lateinit var signs: Array<Sign>
    private var certPassword = ""
    var ownMarkerColor = MutableLiveData(R.color.blue)
    var otherMarkerColor = MutableLiveData(R.color.red)
    var signSize = 64


    fun setOtherMarkerColor(colorId: Int) {
        otherMarkerColor.postValue(colorId)
    }

    fun getOtherMarkerColor(): Int {
        return otherMarkerColor.value!!
    }

    fun setOwnMarkerColor(colorId: Int) {
        ownMarkerColor.postValue(colorId)
    }

    fun getOwnMarkerColor(): Int {
        return ownMarkerColor.value!!
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