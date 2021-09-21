package com.example.smpt

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.smpt.receivers.ForegroundOnlyBroadcastReceiver
import com.example.smpt.remote.RetrofitClient
import com.example.smpt.ui.login.LoginViewModel
import com.example.smpt.ui.main.MainViewModel
import com.example.smpt.ui.map.MapViewModel
import com.example.smpt.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class SMPTApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@SMPTApplication)
            modules(appModule)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val appModule = module {

        single { SharedPreferencesStorage(this@SMPTApplication) }
        single { RetrofitClient().create(this@SMPTApplication, get() ) }
        single { ForegroundOnlyBroadcastReceiver(get(), get()) }

        factory { MapViewModel() }
        factory { MainViewModel(get(), get()) }
        factory { LoginViewModel(get()) }
        factory { SettingsViewModel() }
    }
}