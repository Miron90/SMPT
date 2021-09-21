package com.example.smpt

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.MutableLiveData
import com.example.smpt.models.Sign
import java.security.PrivateKey
import java.security.cert.X509Certificate

class SharedPreferencesStorage (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)

    private lateinit var signs: Array<Sign>
    private var alias = ""
    private lateinit var certChain:Array<X509Certificate?>
    private lateinit var privateKey: PrivateKey

    fun setChain(password: Array<X509Certificate?>) {
        certChain = password
    }

    fun getAlias(): String {
        return alias
    }

    fun setAlias(password: String) {
        alias = password
    }

    fun getChain(): Array<X509Certificate?> {
        return certChain
    }
    fun setKey(password: PrivateKey) {
        privateKey = password
    }

    fun getKey(): PrivateKey {
        return privateKey
    }
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