package com.example.smpt.ui.login

import android.content.Intent
import android.os.Bundle
import android.security.KeyChain
import android.security.KeyChain.getCertificateChain
import android.security.KeyChain.getPrivateKey
import android.security.KeyChainAliasCallback
import android.security.KeyChainException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.smpt.databinding.ActivityLoginBinding
import com.example.smpt.ui.main.MainActivity
import org.koin.android.ext.android.get
import java.security.PrivateKey
import java.security.cert.X509Certificate

import com.example.smpt.SharedPreferencesStorage
import org.koin.android.ext.android.inject
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory


class LoginActivity : AppCompatActivity(), KeyChainAliasCallback {
    private lateinit var viewModel: LoginViewModel
    private val sharedPreferences: SharedPreferencesStorage by inject()
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private var alias: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        KeyChain.choosePrivateKeyAlias(
            this,
            {
                if (it != null) {
                    alias = it
                }
            },
            null,  // List of acceptable key types. null for any
            null,  // issuer, null for any
            null,  // host name of server requesting the cert, null if unavailable
            -1,  // port of server requesting the cert, -1 if unavailable
            ""
        ) // alias to preselect, null if unavailable



        viewModel = get()

        viewModel.success.observe(this, {
            if(it)
                startActivity(Intent(this, MainActivity::class.java))
        })

        binding.btnLogin.isEnabled = false

        binding.inputLogin.doOnTextChanged {
            text, start, before, count ->
            binding.btnLogin.isEnabled = text!!.isNotEmpty()
        }

        binding.btnLogin.setOnClickListener {
            attemptLogin(binding.inputLogin.text.toString(), binding.inputCert.text.toString())
        }
    }

    private fun attemptLogin(login: String, alias:String) {
        Log.d("kluczyk", alias)
        var privateKey: PrivateKey? =null
        var chain:Array<X509Certificate>?
        Thread {
            if (isKeyChainAccessible()) {
                // Key chain installed. Disable the install button and print
                // the key chain information
                    sharedPreferences.setAlias(alias)
                printInfo()
            } else {
                Log.d("kluczyk", "Key Chain is not accessible")
            }
        }.start()
//        var keyGainer = KeyGainer(this, alias)
//        var threa = Thread(keyGainer)
//        threa.start()
//        threa.join()
//        privateKey=keyGainer.getKey()

        Log.d("kluczyk", privateKey.toString())
        viewModel.attemptLogin(login)
    private fun attemptLogin(login: String, cert: String) {
        viewModel.attemptLogin(login, cert)
    }

    override fun alias(alias: String?) {
        if (alias != null) {
            setAlias(alias); // Set the alias in the application preference
            printInfo();
        } else {
            Log.d("kluczyk", "User hit Disallow");
        }
    }

    private fun setAlias(alias: String) {
        this.alias=alias
    }
    private fun printInfo() {
        val alias: String = this.alias
        val certs = getCertificateChain(alias)
        val privateKey = getPrivateKey(alias)
        sharedPreferences.setChain(certs!!)
        sharedPreferences.setKey(privateKey!!)
        val sb = StringBuffer()
        for (cert in certs!!) {
            sb.append(cert?.issuerDN)
            sb.append("\n")
        }
        runOnUiThread {
//            val certTv = findViewById<View>(R.id.cert) as TextView
//            val privateKeyTv = findViewById<View>(R.id.private_key) as TextView
            Log.d("kluczyk123", sb.toString());
            Log.d("kluczyk1234", privateKey!!.format + ":" + privateKey);
        }
    }
    private fun getCertificateChain(alias: String): Array<X509Certificate?>? {
        try {
            return getCertificateChain(this, alias)
        } catch (e: KeyChainException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }
    private fun getPrivateKey(alias: String): PrivateKey? {
        try {
            return getPrivateKey(this, alias)
        } catch (e: KeyChainException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }
    private fun isKeyChainAccessible(): Boolean {
        return (getCertificateChain(this.alias) != null
                && getPrivateKey(this.alias) != null)
    }

}
