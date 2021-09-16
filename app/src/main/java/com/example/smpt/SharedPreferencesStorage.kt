package com.example.smpt

import android.content.Context
import com.example.smpt.models.Sign

class SharedPreferencesStorage (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)

    private lateinit var signs: Array<Sign>

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