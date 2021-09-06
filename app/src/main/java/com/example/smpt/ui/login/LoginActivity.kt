package com.example.smpt.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smpt.databinding.ActivityLoginBinding
import com.example.smpt.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.success.observe(this, {
            if(it)
                startActivity(Intent(this, MainActivity::class.java))
        })

        binding.btnLogin.setOnClickListener {
            attemptLogin(binding.inputLogin.text.toString(), binding.inputPassword.text.toString())
        }
    }

    private fun attemptLogin(login: String, password: String) {
        viewModel.attemptLogin(login, password)
    }
}