package com.example.smpt.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.smpt.databinding.ActivityLoginBinding
import com.example.smpt.ui.main.MainActivity
import org.koin.android.ext.android.get

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

    private fun attemptLogin(login: String, cert: String) {
        viewModel.attemptLogin(login, cert)
    }
}
