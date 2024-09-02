package com.example.usthb9raya.dataClass

import android.net.Uri

data class Contribution(val fullName: String,
                        val email: String,
                        val faculty: String,
                        val module: String,
                        val type: String,
                        val comment: String?,
                        val fileUrl: String,
                        val contributionId: String,
    val mimeType: String    ,
                        val timestamp: Long = System.currentTimeMillis(),
                        val fileDownloaded: String = "false"
)
