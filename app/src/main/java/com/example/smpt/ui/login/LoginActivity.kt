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
            null,
            null,
            null,
            -1,
            ""
        )



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
            attemptLogin(binding.inputLogin.text.toString(),this.alias)
        }
    }

    private fun attemptLogin(login: String, alias:String) {

        Thread {
            if (isKeyChainAccessible()) {
                sharedPreferences.setAlias(alias)
                printInfo()
            }
        }.start()

        viewModel.attemptLogin(login)
    }

    override fun alias(alias: String?) {
        if (alias != null) {
            this.alias=alias
            printInfo();
        }
    }
    private fun printInfo() {
        val alias: String = this.alias
        val certs = getCertificateChain(alias)
        val privateKey = getPrivateKey(alias)
        sharedPreferences.setChain(certs!!)
        sharedPreferences.setAlias(this.alias)
        sharedPreferences.setKey(privateKey!!)
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
