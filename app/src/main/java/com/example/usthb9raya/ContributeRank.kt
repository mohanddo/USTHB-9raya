package com.example.usthb9raya

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.usthb9raya.databinding.ActivityContributeRankBinding
import com.example.usthb9raya.databinding.ActivityMainBinding

class ContributeRank : AppCompatActivity() {
    private lateinit var binding: ActivityContributeRankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContributeRankBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contribute_rank)

    }
}