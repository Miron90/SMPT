package com.example.smpt

import android.content.Context
import android.security.KeyChain
import android.util.Log
import java.security.PrivateKey
import java.security.cert.X509Certificate

class KeyGainer: Runnable{
    @Volatile
    private var key: PrivateKey? = null
    @Volatile
    private var chain: Array<X509Certificate>? = null
    private var context:Context?= null
    private var alias:String =""

    constructor(context: Context, alias:String) {
        this.context = context
        this.alias = alias
    }

    override fun run() {
        Log.d("kluczyk", context.toString())
        key = KeyChain.getPrivateKey(context!!, alias)
        Log.d("kluczyk", "chain")
        chain= KeyChain.getCertificateChain(context!!, alias)
        Log.d("kluczyk", alias)
    }

    fun getKey(): PrivateKey? {
        return key
    }
    fun getChain(): Array<X509Certificate>? {
        return chain
    }
}