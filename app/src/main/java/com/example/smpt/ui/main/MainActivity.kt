package com.example.smpt.ui.main

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.smpt.R

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.showTargetsButton).setOnClickListener {
            Toast.makeText(this, "Troops has been shown", Toast.LENGTH_LONG).show()
        }
    }
}