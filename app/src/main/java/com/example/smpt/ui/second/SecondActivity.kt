package com.example.smpt.ui.second

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smpt.R
import com.example.smpt.databinding.ActivitySecondBinding
import com.example.smpt.ui.main.MainActivity
import com.example.smpt.ui.main.MainViewModel

class SecondActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding: ActivitySecondBinding by lazy {
        ActivitySecondBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.goToMainActivityButton.setOnClickListener {
            buttonClickGoToMain()
        }
    }

    fun buttonClickGoToMain()
    {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
    }
}