package com.example.smpt.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.smpt.ui.Constants

class LoginViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    val success = MediatorLiveData<Boolean>()

    fun attemptLogin(login: String) {
        if(login.isNotEmpty()) {
            sharedPreferences.edit().putString(Constants().USERNAME, login).apply()
            success.postValue(true)
        }
        else
            success.postValue(false)
    }
}