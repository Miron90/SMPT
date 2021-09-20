package com.example.smpt.remote

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.smpt.R
import com.example.smpt.SharedPreferencesStorage
import com.example.smpt.ui.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import android.security.keystore.KeyProperties

import android.security.KeyChainAliasCallback

import android.security.KeyChain
import androidx.annotation.RequiresApi
import com.example.smpt.SystemKeyManager
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import java.security.PrivateKey
import java.security.cert.X509Certificate


class RetrofitClient {


    @RequiresApi(Build.VERSION_CODES.M)
    fun create(context: Context, sharedPreferencesStorage: SharedPreferencesStorage) : ApiInterface {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants().BASE_URL)
            .client(generateSecureOkHttpsClient(context, sharedPreferencesStorage))
            .build()

        return retrofit.create(ApiInterface::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateSecureOkHttpsClient(context: Context, sharedPreferencesStorage: SharedPreferencesStorage): OkHttpClient {

        val httpsClientBuilder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60,TimeUnit.SECONDS)

        val trusted = CustomTrustManager()
        val caFileInputStream = context.resources.openRawResource(R.raw.child)
        val keyStore = KeyStore.getInstance("PKCS12")

//        keyStore.load(caFileInputStream, sharedPreferencesStorage.getCertPassword().toCharArray())

//        val tmfAlgo = TrustManagerFactory.getDefaultAlgorithm()
//        val tmf = TrustManagerFactory.getInstance(tmfAlgo)
//        tmf.init(keyStore)
////
////
//        val keyManagerFactory = KeyManagerFactory.getInstance("X509")
//        keyManagerFactory.init(keyStore, sharedPreferencesStorage.getCertPassword().toCharArray())
//
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(keyManagerFactory.keyManagers, arrayOf(trusted), SecureRandom())
//        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
//        HttpsURLConnection.setDefaultHostnameVerifier{ p0, p1 -> true }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(arrayOf(SystemKeyManager(sharedPreferencesStorage)), arrayOf(trusted), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier{ p0, p1 -> true }
        httpsClientBuilder.sslSocketFactory(sslContext.socketFactory, trusted as X509TrustManager)


        return httpsClientBuilder.hostnameVerifier { p0, p1 -> true }.build()

    }
}