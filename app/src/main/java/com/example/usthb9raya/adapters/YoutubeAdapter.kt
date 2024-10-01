package com.example.usthb9raya.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.R
import com.example.usthb9raya.Utils.Utils
import com.example.usthb9raya.dataClass.YouTube

class YoutubeAdapter(
    private val items: List<YouTube>,
    private val context: Context
) : RecyclerView.Adapter<YoutubeAdapter.YouTubeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YouTubeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.youtube_row, parent, false)
        return YouTubeViewHolder(view)
    }

    override fun onBindViewHolder(holder: YouTubeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class YouTubeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoTitle: TextView = itemView.findViewById(R.id.text_view_youtube)
        private val video: LinearLayout = itemView.findViewById(R.id.youtube_click)

        fun bind(youtubeVideo: YouTube) {
            videoTitle.text = youtubeVideo.videoName
            // Handle click to open the YouTube video link
            video.setOnClickListener {
                // Use the YouTube link from the YouTube object and open it using Utils function
                Utils.openYouTubeLink(itemView.context, youtubeVideo.youTubeLink)
            }
        }
    }
}
