package com.example.smpt

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyChain
import android.security.KeyChainAliasCallback
import android.security.KeyChainException
import android.util.Log
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import java.lang.AssertionError
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.X509KeyManager


/**
 * Wrapper for system key managers. For android before 4.0, no system key
 * manager available.
 *
 */
internal class SystemKeyManager(sharedPreferences: SharedPreferencesStorage) : X509KeyManager {
    // A bridge variable to pass alias from KeyChain dialog to this class
    // instance.
    private var _tempAliasForDialog: String? = null
    private var _notified = false
//    private val _alias: String
    private lateinit var sharedPreferences: SharedPreferencesStorage




    /**
     * Popup the Android system key manager dialog to initialize the Android
     * system KeyChain. It must be called once before any KeyChain method is
     * called. Otherwise, users could not use this feature.
     *
     * @return The alias of the selected certificate.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun initializeKeyChain(): String? {

        // Check whether SystemKeyManager already got an alias
        var storedAlias: String? = null
        if (_sharedPrefs != null) {
            storedAlias = _sharedPrefs!!.getString(
                SYSTEM_KEY_MANAGER_SHARED_PREFERENCES_SELECTED_ALIAS, null
            )
            if (storedAlias != null) {
                return storedAlias
            }
        }

        // Synchronize with the class itself
//        synchronized(SystemKeyManager::class.java) {
//            val keyTypes = arrayOf("RSA")
//            val host: String? = null
//            val port = -1
//            _notified = false
//            KeyChain.choosePrivateKeyAlias(_containerActivity,
//                { selectedAlias ->
//                    _tempAliasForDialog = selectedAlias
//                    _notified = true
//                    synchronized(this@SystemKeyManager) { this@SystemKeyManager.notifyAll() }
//                }, keyTypes, null, host, port, null
//            )
//            try {
//                // Make the thread wait for the system keychain dialog to be
//                // closed.
//                synchronized(this) {
//                    while (!_notified) {
//                        this.wait()
//                    }
//                }
//            } catch (e: InterruptedException) {
//                Log.e("CertProvider", "InteruptedException: $e")
//            }
//            _sharedPrefs!!.edit().putString(
//                SYSTEM_KEY_MANAGER_SHARED_PREFERENCES_SELECTED_ALIAS,
//                _tempAliasForDialog
//            ).apply()
//            return _tempAliasForDialog
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.net.ssl.X509KeyManager#chooseClientAlias(java.lang.String[],
     * java.security.Principal[], java.net.Socket)
     */
    override fun chooseClientAlias(
        keyType: Array<String>,
        issuers: Array<Principal>,
        socket: Socket
    ): String {
        return sharedPreferences.getAlias()
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String,
     * java.security.Principal[], java.net.Socket) This is only required for
     * server applications.
     */
    override fun chooseServerAlias(
        keyType: String,
        issuers: Array<Principal>,
        socket: Socket
    ): String? {
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun getCertificateChain(alias: String): Array<X509Certificate?> {
        return sharedPreferences.getChain()
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String,
     * java.security.Principal[]) The Android KeyChain does not support this
     * feature, so we could not provide it either.
     */
    override fun getClientAliases(keyType: String, issuers: Array<Principal>): Array<String> {
        return arrayOf(sharedPreferences.getAlias())
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String,
     * java.security.Principal[]) This is only required for server applications.
     */
    override fun getServerAliases(keyType: String, issuers: Array<Principal>): Array<String>? {
        return null
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun getPrivateKey(alias: String): PrivateKey? {
        // There is a bug in Android 4.1 that will cause the call to
        // KeyChain.getPrivateKey to result in a segfault later. For more info
        // see https://code.google.com/p/android/issues/detail?id=36545 So,
        // check if the Android version is 4.1, and if it is just return null.
        // It will still cause CertficateFromStore to fail, but at least it will
        // fail less spectacularly.
        if (Build.VERSION.RELEASE.startsWith("4.1")) {
            Log.e(
                "CertProvider",
                "Returning null for the private key. Due to an issue with Android 4.1, getting the real private key would cause this app to crash."
            )
            return null
        }
        var pk: PrivateKey? = null
//        try {
//            pk = KeyChain.getPrivateKey(_containerActivity, alias)
//        } catch (e: KeyChainException) {
//            // We no longer have permission to use the alias, must show the cert picker again.
//            if (_sharedPrefs != null) {
//                _sharedPrefs!!.edit().remove(SYSTEM_KEY_MANAGER_SHARED_PREFERENCES_SELECTED_ALIAS)
//                    .apply()
//            }
//            val newAlias = initializeKeyChain()
//            if (newAlias != null) {
//                try {
//                    pk = KeyChain.getPrivateKey(_containerActivity, newAlias)
//                } catch (e1: KeyChainException) {
//                    Log.e(
//                        "CertProvider",
//                        "KeyChainException while getting private key, even after reinitializing the keychain: $e"
//                    )
//                } catch (e1: InterruptedException) {
//                    Log.e(
//                        "CertProvider",
//                        "InteruptedException while getting private key: $e"
//                    )
//                }
//            }
//        } catch (e: InterruptedException) {
//            Log.e("CertProvider", "InteruptedException while getting private key: $e")
//        } catch (e: AssertionError) {
//            // An assertion error means something went terribly wrong.  This has only been observed
//            // on a Samsung A3.  Instead of crashing, just log the error and return null for the
//            // private key (pk should still be null).
//            Log.e("CertProvider", "AssertionError while getting private key: " + e.localizedMessage)
//        }
        return sharedPreferences.getKey()
    }

    companion object {
        private var _sharedPrefs: SharedPreferences? = null
        private const val SYSTEM_CERT_PROVIDER_SHARED_PREFERENCES =
            "SYSTEM_CERT_PROVIDER_SHARED_PREFERENCES"
        private const val SYSTEM_KEY_MANAGER_SHARED_PREFERENCES_SELECTED_ALIAS = "selectedAlias"
        fun removeStoredAlias() {
            if (_sharedPrefs != null) {
                _sharedPrefs!!.edit().remove(SYSTEM_KEY_MANAGER_SHARED_PREFERENCES_SELECTED_ALIAS)
                    .apply()
            }
        }
    }

    /**
     * Construct a instance of system key manager with specified certificate
     * source descriptor.
     *
     * @param containingWebView
     * the context of the containing WebView.
     */
    init {
        this.sharedPreferences = sharedPreferences
//        if (_sharedPrefs == null) {
//            _sharedPrefs = _containerActivity.getSharedPreferences(
//                SYSTEM_CERT_PROVIDER_SHARED_PREFERENCES, Context.MODE_PRIVATE
//            )
//        }
//        _alias = initializeKeyChain()!!
    }
}