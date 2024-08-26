package com.example.usthb9raya

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

        val faculties = listOf(
            DataClassFaculty("Science", listOf(
                DataClassModule("Mathematics", "Course A", "TP A", "TD A", listOf(
                    DataClassSousModule("akli", "akli course", "akli tp", "akli td")
                )),
                DataClassModule("Physics", "Course B", "TP B", "TD B", emptyList())  // Empty submodule list
            ))
        )

        val facultyAdapter = AdapterFaculty(faculties)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = facultyAdapter


    }
}