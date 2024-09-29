package com.example.usthb9raya

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.adapters.YoutubeAdapter // Use your YoutubeAdapter here
import com.example.usthb9raya.dataClass.YouTube
import com.example.usthb9raya.databinding.ActivityYouTubeBinding
import com.example.usthb9raya.fragments.HomeFragment
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStream

class YouTubeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYouTubeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: YoutubeAdapter // Ensure you're using the correct adapter
    private lateinit var noItemsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYouTubeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
        }



        // Get the module name from the intent
        val moduleNameYoutube = intent.getStringExtra("MODULE_NAME")

        // Load YouTube videos from the raw resource, filtering by title
        val youTubeVideos = loadYouTubeVideos(this, moduleNameYoutube)

        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerView_youtube)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the adapter with the filtered list of videos
        adapter = YoutubeAdapter(youTubeVideos, this)
        recyclerView.adapter = adapter

        // Initialize the no items TextView
        noItemsTextView = findViewById(R.id.no_items_text_view)

        // Check if the list is empty and adjust visibility accordingly
        if (youTubeVideos.isEmpty()) {
            recyclerView.visibility = RecyclerView.GONE
            noItemsTextView.visibility = TextView.VISIBLE
        } else {
            recyclerView.visibility = RecyclerView.VISIBLE
            noItemsTextView.visibility = TextView.GONE
        }

        // Set the title, using a default if null
        binding.youtubeTitle.text = moduleNameYoutube ?: "YouTube Link Unknown"
    }


    private fun loadYouTubeVideos(context: Context, titleFilter: String?): List<YouTube> {

        val inputStream: InputStream = context.resources.openRawResource(R.raw.youtube_videos)

        val jsonString = inputStream.bufferedReader().use { it.readText() }


        val listType = object : TypeToken<List<YouTube>>() {}.type

        val allVideos: List<YouTube> = Gson().fromJson(jsonString, listType)

        return if (!titleFilter.isNullOrEmpty()) {
            allVideos.filter { it.filterTitle.equals(titleFilter, ignoreCase = true) } // Assuming 'title' is the field in YouTube data class
        } else {
            allVideos
        }
    }


}



