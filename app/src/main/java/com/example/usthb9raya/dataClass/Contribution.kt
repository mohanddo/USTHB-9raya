package com.example.usthb9raya.dataClass

data class Contribution(val fullName: String,
                        val email: String,
                        val faculty: String,
                        val module: String,
                        val type: String,
                        val comment: String,
                        val fileUrls: List<String>,
                        val fileNames: List<String>,
                        val contributionId: String,
    val mimeTypes: List<String>,
                        val timestamp: Long = System.currentTimeMillis(),
)
