package com.example.usthb9raya

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.adapters.YoutubeAdapter
import com.example.usthb9raya.dataClass.YouTube
import com.example.usthb9raya.databinding.ActivityYouTubeBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class YouTubeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYouTubeBinding
    private lateinit var adapter: YoutubeAdapter
    private val TAG = "YouTubeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYouTubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge layout
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Get the module name from the intent
        val moduleNameYoutube = intent.getStringExtra("MODULE_NAME")

        // Set the module name as the YouTube title
        binding.youtubeTitle.text = moduleNameYoutube

        if (isLocalFileAvailable(this)) {
            // Load YouTube videos from local storage with filtering
            val localYouTubeVideos = loadYouTubeVideos(this)

            // Fetch YouTube videos from Firebase Storage and compare with local data (in the background)
            fetchYouTubeVideosFromFirebase { firebaseYouTubeVideos ->
                val mergedVideos = mergeYouTubeData(localYouTubeVideos, firebaseYouTubeVideos)
                updateLocalFile(this, mergedVideos) // Update the local file with the merged data
                displayYouTubeVideos(mergedVideos, moduleNameYoutube) // Display merged videos
            }
        } else {
            // First-time entry, download the file from Firebase
            showProgressBar(true) // Show progress bar for first-time download

            fetchYouTubeVideosFromFirebase { firebaseYouTubeVideos ->
                if (firebaseYouTubeVideos.isNotEmpty()) {
                    // Save the fetched data to local storage and display it
                    updateLocalFile(this, firebaseYouTubeVideos)
                    displayYouTubeVideos(firebaseYouTubeVideos, moduleNameYoutube)
                } else {
                    showNoItemsText(true) // Show "no items" if Firebase data is also empty
                }
                showProgressBar(false) // Hide progress bar once the download is done
            }
        }
    }

    // Method to check if the local file is available
    private fun isLocalFileAvailable(context: Context): Boolean {
        val localFile = File(context.filesDir, "youtube_videos.json")
        return localFile.exists()
    }

    // Method to load local YouTube videos
    private fun loadYouTubeVideos(context: Context): List<YouTube> {
        val localFile = File(context.filesDir, "youtube_videos.json")
        if (!localFile.exists()) return emptyList()

        val jsonString = localFile.bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<YouTube>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    // Method to fetch YouTube videos from Firebase Storage
    private fun fetchYouTubeVideosFromFirebase(onResult: (List<YouTube>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("youtube_videos.json") // Correct path to your file in Firebase Storage

        // Create a temporary file to download the file from Firebase Storage
        val localFile = File.createTempFile("youtube_videos", "json")

        storageRef.getFile(localFile).addOnSuccessListener {
            // Log the contents of the file to check if it's downloaded correctly
            val jsonString = localFile.bufferedReader().use { it.readText() }
            Log.d(TAG, "Downloaded JSON: $jsonString") // Log the JSON content

            // Parse the content
            val firebaseYouTubeVideos = parseYouTubeVideosFromFile(localFile)
            onResult(firebaseYouTubeVideos)
        }.addOnFailureListener { exception ->
            // Handle any errors here (e.g., network failure)
            Log.e(TAG, "Failed to download file from Firebase Storage", exception)
            showNoItemsText(true) // Show no items text on failure
            onResult(emptyList()) // Return an empty list in case of failure
        }
    }

    // Parse the downloaded file into a list of YouTube objects
    private fun parseYouTubeVideosFromFile(file: File): List<YouTube> {
        val jsonString = file.bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<YouTube>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    // Method to merge local and Firebase data based on the YouTube link
    private fun mergeYouTubeData(localVideos: List<YouTube>, firebaseVideos: List<YouTube>): List<YouTube> {
        // Create a mutable list to hold the merged videos
        val mergedVideos = mutableListOf<YouTube>()

        // Add all local videos to the list
        mergedVideos.addAll(localVideos)

        // Add Firebase videos that don't exist in the local list (compare by link)
        for (firebaseVideo in firebaseVideos) {
            if (localVideos.none { it.youTubeLink == firebaseVideo.youTubeLink }) {
                mergedVideos.add(firebaseVideo)
            }
        }

        return mergedVideos
    }

    // Display YouTube videos with filtering based on module name
    private fun displayYouTubeVideos(videos: List<YouTube>, titleFilter: String?) {
        // Initialize RecyclerView and set its layout manager
        binding.recyclerViewYoutube.layoutManager = LinearLayoutManager(this)

        // Filter videos based on the titleFilter
        val filteredVideos = if (!titleFilter.isNullOrEmpty()) {
            videos.filter { it.filterTitle.contains(titleFilter, ignoreCase = true) }
        } else {
            videos // Return all videos if no filter is provided
        }

        // Update the adapter with the list of filtered videos
        adapter = YoutubeAdapter(filteredVideos, this)
        binding.recyclerViewYoutube.adapter = adapter

        // Show the no items text view if the list is empty
        showNoItemsText(filteredVideos.isEmpty())
    }

    // Save the merged data to the local file
    private fun updateLocalFile(context: Context, videos: List<YouTube>) {
        val file = File(context.filesDir, "youtube_videos.json")
        file.writeText(Gson().toJson(videos))
    }

    // Show or hide the progress bar
    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    // Show or hide the "no items" text view
    private fun showNoItemsText(show: Boolean) {
        binding.noItemsTextView.visibility = if (show) View.VISIBLE else View.GONE
    }
}
