package com.example.usthb9raya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.usthb9raya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

// test to see of the open link file work
        binding.buttOpenDrive.setOnClickListener {
            val driveLink = binding.textLinkDrive.text.toString()
            if (driveLink.isNotEmpty()) {
                openDriveLink(driveLink)
            }
        }

        handleIncomingIntent(intent)
    } // the end of the test it work

    private fun openDriveLink(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    // this fun handle incoming intent  is used to handel if the app is opened from a browser
    private fun handleIncomingIntent(intent: Intent) {
        val action = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            val url = data.toString()
            openDriveLink(url)
        }
    }
}
