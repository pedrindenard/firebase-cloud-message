package com.pdm.firebasecloudmessaging.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pdm.firebasecloudmessaging.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val numberOkCookies = intent.getStringExtra("cookie")

        val cookies = Integer.valueOf(numberOkCookies!!)

        Toast.makeText(
            this@NotificationActivity,
            if (cookies < 50) {
                "You get small bonus."
            } else {
                "You get HUGE bonus!"
            },
            Toast.LENGTH_SHORT
        ).show()
    }
}