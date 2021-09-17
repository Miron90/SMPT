package com.example.smpt

import android.app.Application
import com.example.smpt.receivers.ForegroundOnlyBroadcastReceiver
import com.example.smpt.remote.ApiInterface
import com.example.smpt.remote.RetrofitClient
import com.example.smpt.ui.login.LoginViewModel
import com.example.smpt.ui.main.MainActivity
import com.example.smpt.ui.main.MainViewModel
import com.example.smpt.ui.map.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class SMPTApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@SMPTApplication)
            modules(appModule)
        }
    }

    private val appModule = module {

        single { SharedPreferencesStorage(this@SMPTApplication) }
        single { RetrofitClient().create(this@SMPTApplication, get()) }
        single { ForegroundOnlyBroadcastReceiver(get(), get()) }

        factory { MapViewModel() }
        factory { MainViewModel(get(), get()) }
        factory { LoginViewModel(get()) }
    }
}