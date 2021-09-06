package com.example.smpt.ui.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    val success = MediatorLiveData<Boolean>()

    fun attemptLogin(login: String, password: String) {
        if(login.isNotEmpty() && password.isNotEmpty())
            success.postValue(true)
        else
            success.postValue(false)
    }
}