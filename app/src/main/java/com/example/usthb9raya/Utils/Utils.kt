package com.example.usthb9raya.Utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity

object Utils {
    fun openDriveLink(context: Context, url: String?) {
        if (url != null) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        } else {
            Log.e("Utils", "URL is null")
        }
    }
}