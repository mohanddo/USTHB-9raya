package com.example.usthb9raya

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.adapters.AdapterFaculty
import com.example.usthb9raya.dataClass.DataClassFaculty
import com.example.usthb9raya.dataClass.DataClassModule
import com.example.usthb9raya.dataClass.DataClassSousModule
import com.example.usthb9raya.databinding.ActivityMain2Binding
import org.json.JSONArray
import java.io.InputStream

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Load JSON data and parse it
        val jsonArray = readJsonFromRaw(this, R.raw.drive_data)
        val faculties = parseJson(jsonArray)

        // Set the adapter with parsed data
        binding.recyclerView.adapter = AdapterFaculty(faculties)
    }

    // Function to read JSON data from the raw resource file
    private fun readJsonFromRaw(context: Context, resourceId: Int): JSONArray {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val json = inputStream.bufferedReader().use { it.readText() }
        return JSONArray(json)
    }

    // Function to parse JSON data into a list of DataClassFaculty objects
    private fun parseJson(jsonArray: JSONArray): List<DataClassFaculty> {
        val facultyList = mutableListOf<DataClassFaculty>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val facultyName = jsonObject.getString("facultyName")
            val modulesArray = jsonObject.getJSONArray("modules")
            val moduleList = mutableListOf<DataClassModule>()
            for (j in 0 until modulesArray.length()) {
                val moduleObject = modulesArray.getJSONObject(j)
                val moduleName = moduleObject.getString("moduleName")
                val courseName = moduleObject.getString("courseName")
                val tpName = moduleObject.getString("tpName")
                val tdName = moduleObject.getString("tdName")
                val sousModulesArray = moduleObject.getJSONArray("sousModules")
                val sousModuleList = mutableListOf<DataClassSousModule>()
                for (k in 0 until sousModulesArray.length()) {
                    val sousModuleObject = sousModulesArray.getJSONObject(k)
                    val sousModuleName = sousModuleObject.getString("sousModuleName")
                    val sousModuleCourse = sousModuleObject.getString("sousModuleCourse")
                    val sousModuleTp = sousModuleObject.getString("sousModuleTp")
                    val sousModuleTd = sousModuleObject.getString("sousModuleTd")
                    sousModuleList.add(DataClassSousModule(sousModuleName, sousModuleCourse, sousModuleTp, sousModuleTd))
                }
                moduleList.add(DataClassModule(moduleName, courseName, tpName, tdName, sousModuleList))
            }
            facultyList.add(DataClassFaculty(facultyName, moduleList))
        }
        return facultyList
    }
}
