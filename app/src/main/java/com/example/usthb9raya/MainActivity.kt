package com.example.usthb9raya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.usthb9raya.Utils.Utils.openDriveLink
import com.example.usthb9raya.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

        binding.buttOpenDrive.setOnClickListener {
            val driveLink = binding.textLinkDrive.text.toString()
            openDriveLink(this, driveLink)
        }

    }
}
