package com.example.usthb9raya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.usthb9raya.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PICK_FILE_REQUEST = 1
    private var fileUri: Uri? = null

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
            } else {
                // Handle the case where the URL is empty
                Toast.makeText(this, "URL is empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttOpenFilePicker.setOnClickListener {
            openFilePicker()
        }

        binding.buttSendEmailUpload.setOnClickListener {
            fileUri?.let { uri ->
                sendEmailWithAttachment(uri)
                binding.textViewLink.text = uri.toString() // Display the link in TextView
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

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }



    @Deprecated("This method is deprecated, use registerForActivityResult() instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST) {
            if (resultCode == RESULT_OK) {
                fileUri = data?.data
                if (fileUri != null) {
                    // Successfully retrieved the file URI
                    binding.textViewLink.text = fileUri.toString()
                } else {
                    // File URI is null
                    Toast.makeText(this, "Failed to retrieve file URI", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Result was not OK
                Toast.makeText(this, "File selection was canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun sendEmailWithAttachment(uri: Uri) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "vnd.android.cursor.dir/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("mahfoufakli2@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "File Upload")
            putExtra(Intent.EXTRA_TEXT, "Please find the attached file.")
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }
}
