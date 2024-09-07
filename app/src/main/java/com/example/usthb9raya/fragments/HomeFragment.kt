package com.example.usthb9raya.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usthb9raya.R
import com.example.usthb9raya.adapters.UnifiedAdapter
import com.example.usthb9raya.dataClass.Faculty
import com.example.usthb9raya.dataClass.Module
import com.example.usthb9raya.dataClass.SousModule
import com.example.usthb9raya.databinding.FragmentHomeBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("faculties.json")
    private var localFile: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        localFile = File(requireContext().filesDir, "faculties_modules.json")

        showLocalData()

        if (isNetworkAvailable()) {
            checkForUpdatesFromFirebaseStorage { isUpdated ->
                if (isUpdated) {

                    binding.progressBar.visibility = View.VISIBLE
                    fetchJsonFromFirebaseStorage { firebaseJsonArray ->
                        val localJsonArray = readJsonFromFile(localFile!!)
                        syncLocalWithFirebase(localJsonArray, firebaseJsonArray)

                        binding.progressBar.visibility = View.GONE

                        binding.recyclerView.adapter = UnifiedAdapter(parseJson(localJsonArray), requireContext())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
    }

    private fun showLocalData() {

        if (localFile!!.exists()) {
            val localJsonArray = readJsonFromFile(localFile!!)
            binding.recyclerView.adapter = UnifiedAdapter(parseJson(localJsonArray), requireContext())
            binding.recyclerView.visibility = View.VISIBLE
        } else {

            Toast.makeText(requireContext(), "No local data found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readJsonFromFile(file: File): JSONArray {
        val jsonString = file.bufferedReader().use { it.readText() }
        return JSONArray(jsonString)
    }

    private fun checkForUpdatesFromFirebaseStorage(onCheckComplete: (Boolean) -> Unit) {
        // Check Firebase Storage metadata without showing a ProgressBar
        storageRef.metadata.addOnSuccessListener { metadata: StorageMetadata ->
            val remoteFileUpdated = metadata.updatedTimeMillis
            val localFileUpdated = localFile?.lastModified() ?: 0

            // If the Firebase file is newer, return true to indicate an update is needed
            if (remoteFileUpdated > localFileUpdated) {
                onCheckComplete(true)
            } else {
                onCheckComplete(false)
            }
        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(requireContext(), "Failed to check file metadata", Toast.LENGTH_SHORT).show()
            onCheckComplete(false)
        }
    }

    private fun fetchJsonFromFirebaseStorage(onDataFetched: (JSONArray) -> Unit) {
        // Download the file from Firebase
        val tempFile = File.createTempFile("faculties_modules", "json")
        storageRef.getFile(tempFile).addOnSuccessListener {
            val jsonString = tempFile.readText()
            saveJsonToFile(JSONArray(jsonString))
            onDataFetched(JSONArray(jsonString))
            tempFile.delete()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to fetch data from Firebase Storage", Toast.LENGTH_SHORT).show()
        }
    }

    private fun syncLocalWithFirebase(localJson: JSONArray, firebaseJson: JSONArray) {
        val (facultiesToAdd, facultiesToRemove) = compareJson(localJson, firebaseJson)

        facultiesToAdd.forEach { faculty ->
            val newFacultyJson = createJsonForFaculty(faculty)
            localJson.put(newFacultyJson)
        }

        for (i in localJson.length() - 1 downTo 0) {
            val localFaculty = localJson.getJSONObject(i)
            val facultyName = localFaculty.getString("faculty")
            if (facultiesToRemove.any { it.faculty_name == facultyName }) {
                localJson.remove(i)
            }
        }

        saveJsonToFile(localJson)
    }

    private fun compareJson(localJson: JSONArray, firebaseJson: JSONArray): Pair<List<Faculty>, List<Faculty>> {
        val localFaculties = parseJson(localJson)
        val firebaseFaculties = parseJson(firebaseJson)

        val facultiesToAdd = firebaseFaculties.filter { firebaseFaculty ->
            localFaculties.none { it.faculty_name == firebaseFaculty.faculty_name }
        }

        val facultiesToRemove = localFaculties.filter { localFaculty ->
            firebaseFaculties.none { it.faculty_name == localFaculty.faculty_name }
        }

        return Pair(facultiesToAdd, facultiesToRemove)
    }

    private fun createJsonForFaculty(faculty: Faculty): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("faculty", faculty.faculty_name)

        val modulesArray = JSONArray()
        for (module in faculty.modules) {
            val moduleObject = JSONObject()
            moduleObject.put("module", module.module_name)
            moduleObject.put("course", module.course_link)
            moduleObject.put("tp", module.tp_link)
            moduleObject.put("td", module.td_link)
            moduleObject.put("exams", module.exams_link)
            moduleObject.put("others", module.others_link)

            val sousModulesArray = JSONArray()
            for (sousModule in module.submodules) {
                val sousModuleObject = JSONObject()
                sousModuleObject.put("sousModuleName", sousModule.sous_module_name)
                sousModuleObject.put("sousModuleCourse", sousModule.sous_module_course_link)
                sousModuleObject.put("sousModuleTp", sousModule.sous_module_tp_link)
                sousModuleObject.put("sousModuleTd", sousModule.sous_module_td_link)
                sousModuleObject.put("sousModuleExams", sousModule.sous_module_exams_link)
                sousModuleObject.put("sousModuleOthers", sousModule.sous_module_others_link)
                sousModulesArray.put(sousModuleObject)
            }
            moduleObject.put("sousModules", sousModulesArray)
            modulesArray.put(moduleObject)
        }
        jsonObject.put("modules", modulesArray)
        return jsonObject
    }

    private fun saveJsonToFile(updatedJson: JSONArray) {
        val file = File(requireContext().filesDir, "faculties_modules.json")
        file.writeText(updatedJson.toString())
    }

    private fun parseJson(jsonArray: JSONArray): List<Faculty> {
        val facultyList = mutableListOf<Faculty>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val facultyName = jsonObject.getString("faculty")
            val modulesArray = jsonObject.getJSONArray("modules")
            val modulesList = mutableListOf<Module>()

            for (j in 0 until modulesArray.length()) {
                val moduleObject = modulesArray.getJSONObject(j)
                val moduleName = moduleObject.getString("module")
                val course = moduleObject.getString("course")
                val tp = moduleObject.getString("tp")
                val td = moduleObject.getString("td")
                val exams = moduleObject.getString("exams")
                val others = moduleObject.getString("others")

                val sousModulesArray = moduleObject.getJSONArray("sousModules")
                val sousModulesList = mutableListOf<SousModule>()

                for (k in 0 until sousModulesArray.length()) {
                    val sousModuleObject = sousModulesArray.getJSONObject(k)
                    val sousModuleName = sousModuleObject.getString("sousModuleName")
                    val sousModuleCourse = sousModuleObject.getString("sousModuleCourse")
                    val sousModuleTp = sousModuleObject.getString("sousModuleTp")
                    val sousModuleTd = sousModuleObject.getString("sousModuleTd")
                    val sousModuleExams = sousModuleObject.getString("sousModuleExams")
                    val sousModuleOthers = sousModuleObject.getString("sousModuleOthers")

                    val sousModule = SousModule(sousModuleName, sousModuleCourse, sousModuleTp, sousModuleTd, sousModuleExams, sousModuleOthers)
                    sousModulesList.add(sousModule)
                }

                val module = Module(moduleName, course, tp, td, exams, others, sousModulesList)
                modulesList.add(module)
            }

            val faculty = Faculty(facultyName, modulesList)
            facultyList.add(faculty)
        }

        return facultyList
    }
}


