package com.example.usthb9raya.dataClass

data class Contribution(val contributionId: String,
    val fullName: String,
                        val email: String,
                        val faculty: String,
                        val module: String,
                        val type: String,
                        val comment: String?,
                        val fileUrls: List<String>? = null,
                        val fileNames: List<String>? = null,
                        val filesSize: Long? = null,
                        val youtubeLink: String? = null,
                        val timestamp: Long = System.currentTimeMillis()
)
