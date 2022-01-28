package com.pdm.firebasecloudmessaging.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pdm.firebasecloudmessaging.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}