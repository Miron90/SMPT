package com.example.smpt

import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.X509KeyManager


internal class SystemKeyManager(sharedPreferences: SharedPreferencesStorage) : X509KeyManager {

    private var sharedPreferences: SharedPreferencesStorage

    override fun chooseClientAlias(
        keyType: Array<String>,
        issuers: Array<Principal>,
        socket: Socket
    ): String {
        return sharedPreferences.getAlias()
    }

    override fun chooseServerAlias(
        keyType: String,
        issuers: Array<Principal>,
        socket: Socket
    ): String? {
        return null
    }

    override fun getCertificateChain(alias: String): Array<X509Certificate?> {
        return sharedPreferences.getChain()
    }

    override fun getClientAliases(keyType: String, issuers: Array<Principal>): Array<String> {
        return arrayOf(sharedPreferences.getAlias())
    }


    override fun getServerAliases(keyType: String, issuers: Array<Principal>): Array<String>? {
        return null
    }


    override fun getPrivateKey(alias: String): PrivateKey? {

        return sharedPreferences.getKey()
    }



    init {
        this.sharedPreferences = sharedPreferences
    }
}