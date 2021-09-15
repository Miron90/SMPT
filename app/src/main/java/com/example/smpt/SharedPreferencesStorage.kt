package com.example.smpt

import android.content.Context

class SharedPreferencesStorage (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "").toString()
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}