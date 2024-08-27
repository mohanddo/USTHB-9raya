package com.example.usthb9raya

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.adapters.AdapterFaculty
import com.example.usthb9raya.dataClass.Faculty
import com.example.usthb9raya.dataClass.Module
import com.example.usthb9raya.dataClass.SousModule
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


        val jsonArray = readJsonFromRaw(requireContext(), R.raw.faculties_modules)
        val faculties = parseJson(jsonArray)

        binding.recyclerView.adapter = AdapterFaculty(faculties)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to read JSON data from the raw resource file
    private fun readJsonFromRaw(context: Context, resourceId: Int): JSONArray {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val json = inputStream.bufferedReader().use { it.readText() }
        return JSONArray(json)
    }

    // Function to parse JSON data into a list of DataClassFaculty objects
    private fun parseJson(jsonArray: JSONArray): List<Faculty> {
        val facultyList = mutableListOf<Faculty>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val faculty = jsonObject.getString("faculty")
            val modulesArray = jsonObject.getJSONArray("modules")
            val moduleList = mutableListOf<Module>()

            for (j in 0 until modulesArray.length()) {
                val moduleObject = modulesArray.getJSONObject(j)
                val module = moduleObject.getString("module")
                val courseLink = moduleObject.getString("course")
                val tpLink = moduleObject.getString("tp")
                val tdLink = moduleObject.getString("td")
                val examsLink = moduleObject.getString("exams")
                val othersLink = moduleObject.getString("others")
                val sousModulesArray = moduleObject.getJSONArray("sousModules")
                val sousModuleList = mutableListOf<SousModule>()

                for (k in 0 until sousModulesArray.length()) {
                    val sousModuleObject = sousModulesArray.getJSONObject(k)
                    val sousModule = sousModuleObject.getString("sousModuleName")
                    val sousModuleCourseLink = sousModuleObject.getString("sousModuleCourse")
                    val sousModuleTpLink = sousModuleObject.getString("sousModuleTp")
                    val sousModuleTdLink = sousModuleObject.getString("sousModuleTd")
                    val sousModuleExamsLink = sousModuleObject.getString("sousModuleExams")
                    val sousModuleOthersLink = sousModuleObject.getString("sousModuleOthers")
                    sousModuleList.add(SousModule(sousModule, sousModuleCourseLink, sousModuleTpLink, sousModuleTdLink,
                        sousModuleExamsLink, sousModuleOthersLink))
                }
                moduleList.add(Module(module, courseLink, tpLink, tdLink, examsLink, othersLink, sousModuleList))
            }
            facultyList.add(Faculty(faculty, moduleList))
        }
        return facultyList
    }

}