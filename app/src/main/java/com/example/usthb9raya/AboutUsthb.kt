package com.example.usthb9raya

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.usthb9raya.databinding.ActivityAboutUsthbBinding
import com.example.usthb9raya.databinding.ActivityContributeRankBinding

class AboutUsthb : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsthbBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsthbBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

    }
}