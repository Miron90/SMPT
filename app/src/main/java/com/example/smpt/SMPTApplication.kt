package com.example.smpt

import android.app.Application
import com.example.smpt.receivers.ForegroundOnlyBroadcastReceiver
import com.example.smpt.remote.ApiInterface
import com.example.smpt.remote.RetrofitClient
import com.example.smpt.ui.main.MainActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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

        single<ApiInterface> { RetrofitClient().create() }

        single { ForegroundOnlyBroadcastReceiver(get()) }
    }
}