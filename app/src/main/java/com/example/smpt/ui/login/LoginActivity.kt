package com.example.smpt.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.smpt.databinding.ActivityLoginBinding
import com.example.smpt.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this,
            LoginViewModelFactory(PreferenceManager.getDefaultSharedPreferences(this)))
            .get(LoginViewModel::class.java)

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
            attemptLogin(binding.inputLogin.text.toString())
        }
    }

    private fun attemptLogin(login: String) {
        viewModel.attemptLogin(login)
    }
}
