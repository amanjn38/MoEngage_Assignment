package com.example.moengageassignment.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.moengageassignment.R
import com.example.moengageassignment.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySplashBinding
    private val DELAY: Long = 2300
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        if (mainLooper != null) {
            Handler(mainLooper).postDelayed({
                moveToHomeScreen()
            }, DELAY)
        }
    }

    private fun moveToHomeScreen() {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}