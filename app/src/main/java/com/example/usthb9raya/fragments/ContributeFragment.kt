package com.example.usthb9raya.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.usthb9raya.R
import com.example.usthb9raya.Utils.FirebaseUtil.contributionsRef
import com.example.usthb9raya.Utils.FirebaseUtil.storageRef
import com.example.usthb9raya.Utils.Utils
import com.example.usthb9raya.Utils.Utils.getFileNameFromUri
import com.example.usthb9raya.Utils.Utils.getMimeType
import com.example.usthb9raya.Utils.Utils.isEditTextEmpty
import com.example.usthb9raya.Utils.Utils.isTextViewEmpty
import com.example.usthb9raya.Utils.Utils.isValidEmail
import com.example.usthb9raya.Utils.Utils.multiChoiceDialog
import com.example.usthb9raya.Utils.Utils.singleChoiceDialog
import com.example.usthb9raya.dataClass.Contribution
import com.example.usthb9raya.databinding.FragmentContributeBinding
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask

class ContributeFragment : Fragment(R.layout.fragment_contribute) {
    private var _binding: FragmentContributeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var faculty: TextView
    private lateinit var module: EditText
    private lateinit var type: TextView
    private lateinit var comment: EditText
    private var fileUris: MutableList<Uri> = mutableListOf()
    private var fileNames: MutableList<String> = mutableListOf()
    private lateinit var progressBar: ProgressBar
    private lateinit var contributeButt: AppCompatButton
    private var uploadTasks: MutableList<UploadTask> = mutableListOf()
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var addFileButt: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContributeBinding.bind(view)

        addFileButt = binding.buttAddFile
        getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if(uris.isNotEmpty()) {
                for(uri in uris) {
                    fileUris.add(uri)
                    fileNames.add(getFileNameFromUri(requireContext(), uri))
                }
                addFileButt.text = getString(R.string.changer_le_fichier)
            }
        }

        Log.e("FileUri", fileUris.toString())
        progressBar = binding.progressBar
        fullName = binding.fullName
        email = binding.email
        faculty = binding.faculty
        module = binding.module
        type = binding.type
        comment = binding.comment


        contributeButt = binding.buttContribute
        contributeButt.setOnClickListener {
            if(!isEditTextEmpty(fullName) &&
                isValidEmail(email) &&
                !isTextViewEmpty(faculty) &&
                !isEditTextEmpty(module) &&
                !isTextViewEmpty(type)
                ) {

                if (fileUris.isNotEmpty()) {
                    handleSelectedFile(fileUris)
                } else {
                    Toast.makeText(requireContext(), "Veuillez sélectionner un fichier.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addFileButt.setOnClickListener {
            openFilePicker()
        }

        val facultyList = resources.getStringArray(R.array.faculties)
        faculty.setOnClickListener {
            singleChoiceDialog(requireContext(), facultyList, "Faculté", faculty)
        }


        val typeList = resources.getStringArray(R.array.type)
        type.setOnClickListener {
            multiChoiceDialog(requireContext(), typeList, "Type", type)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun openFilePicker() {
        getContent.launch("*/*")
    }

    private fun handleSelectedFile(uris: List<Uri>) {

        try {
            val fileSize = getFileSize(uris)

            val maxFileSize = 200 * 1024 * 1024

            if (fileSize > maxFileSize) {
                Toast.makeText(requireContext(), "Les fichiers sont trop volumineux (plus de 200 Mo)", Toast.LENGTH_LONG).show()
                return
            }

            uploadFiles()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Échec de l'ouverture du fichier", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getFileSize(uris: List<Uri>): Long {
        var fileSize: Long = 0
        for (uri in uris) {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {

                    val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        fileSize += it.getLong(sizeIndex)
                    }
                }
            }
        }

        return fileSize
    }

    private fun uploadFiles() {
        contributeButt.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        progressBar.max = fileUris.size * 100

        var totalBytesTransferred: Long = 0
        var totalBytes: Long = 0

        for (uri in fileUris) {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        totalBytes += it.getLong(sizeIndex)
                    }
                }
            }
        }

        val uploadTasks = mutableListOf<UploadTask>()
        val downloadUris = mutableListOf<String>()

        val contributionId = System.currentTimeMillis().toString()
        val filesReference = storageRef.child("uploads").child(contributionId)

        for (uri in fileUris) {
            val fileReference = filesReference.child(getFileNameFromUri(requireContext(), uri))
            val uploadTask = fileReference.putFile(uri)

            uploadTasks.add(uploadTask)
            uploadTask.addOnProgressListener { snapshot ->
                totalBytesTransferred += snapshot.bytesTransferred
                updateProgressBar(totalBytesTransferred, totalBytes)
            }
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        downloadUris.add(downloadUri.toString())
                        if (downloadUris.size == fileUris.size) { // Check if all files are uploaded
                            handleAllUploadsSuccess(contributionId, downloadUris, totalBytes)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    if (exception !is StorageException || exception.message != "User cancelled the upload") {
                        Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    contributeButt.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    progressBar.progress = 0
                    addFileButt.text = getString(R.string.ajouter_un_fichier)
                }
        }
    }

    private fun handleAllUploadsSuccess(contributionId: String, downloadUris: List<String>, fileSizeInBytes: Long) {
        val contribution = Contribution(fullName.text.toString(),
            email.text.toString(),
            faculty.text.toString(),
            module.text.toString(),
            type.text.toString(),
            comment.text.toString(),
            downloadUris,
            fileNames,
            contributionId,
            fileSizeInBytes
        )
        saveFileContributionToDatabase(contribution)
        progressBar.visibility = View.GONE
        contributeButt.visibility = View.VISIBLE
        progressBar.progress = 0
        addFileButt.text = getString(R.string.ajouter_un_fichier)
    }

    private fun updateProgressBar(totalBytesTransferred: Long, totalBytes: Long) {
        val progress = (100.0 * totalBytesTransferred / totalBytes).toInt()
        progressBar.progress = progress
    }

    private fun saveFileContributionToDatabase(contribution: Contribution) {
        contributionsRef.child(contribution.contributionId).setValue(contribution).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Utils.alertDialog(requireContext(), "Merci pour votre contribution", "Vous recevrez un email lorsque votre contribution sera confirmée.",
                    "Ok", null, {
                        reset()
                    }, null)
            } else {
                Log.e("ContributeFragment", "Error saving contribution to database: ${task.exception}")
                Toast.makeText(requireContext(), "Error saving contribution to database", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reset() {
        faculty.hint = "Faculty"
        faculty.text = ""
        module.hint = "Module"
        module.text.clear()
        type.hint = "Type"
        type.text = ""
        comment.hint = "Comment"
        comment.text.clear()
        fileUris = mutableListOf()
        addFileButt.text = getString(R.string.ajouter_un_fichier)
        progressBar.visibility = View.GONE
        progressBar.progress = 0
        contributeButt.visibility = View.VISIBLE
    }
}