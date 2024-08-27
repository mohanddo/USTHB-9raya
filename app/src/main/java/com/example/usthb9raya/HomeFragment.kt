package com.example.usthb9raya

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.adapters.AdapterFaculty
import com.example.usthb9raya.dataClass.DataClassFaculty
import com.example.usthb9raya.dataClass.DataClassModule
import com.example.usthb9raya.dataClass.DataClassSousModule
import com.example.usthb9raya.databinding.FragmentHomeBinding
import org.json.JSONArray
import java.io.InputStream

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val jsonArray = readJsonFromRaw(requireContext(), R.raw.drive_data)
        val faculties = parseJson(jsonArray)

        binding.recyclerView.adapter = AdapterFaculty(faculties, requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun readJsonFromRaw(context: Context, resourceId: Int): JSONArray {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val json = inputStream.bufferedReader().use { it.readText() }
        return JSONArray(json)
    }


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
                val exams = moduleObject.getString("exams")  // Added field
                val others = moduleObject.getString("others") // Added field
                val sousModulesArray = moduleObject.getJSONArray("sousModules")
                val sousModuleList = mutableListOf<DataClassSousModule>()
                for (k in 0 until sousModulesArray.length()) {
                    val sousModuleObject = sousModulesArray.getJSONObject(k)
                    val sousModuleName = sousModuleObject.getString("sousModuleName")
                    val sousModuleCourse = sousModuleObject.getString("sousModuleCourse")
                    val sousModuleTp = sousModuleObject.getString("sousModuleTp")
                    val sousModuleTd = sousModuleObject.getString("sousModuleTd")
                    val sousModuleExams = sousModuleObject.optString("sousModuleExams")
                    val sousModuleOthers = sousModuleObject.optString("sousModuleOthers")
                    sousModuleList.add(
                        DataClassSousModule(
                            sousModuleName, sousModuleCourse, sousModuleTp, sousModuleTd,
                            sousModuleExams, sousModuleOthers
                        )
                    )
                }
                moduleList.add(
                    DataClassModule(
                        moduleName, courseName, tpName, tdName, exams, others, sousModuleList
                    )
                )
            }
            facultyList.add(DataClassFaculty(facultyName, moduleList))
        }
        return facultyList
    }

}
