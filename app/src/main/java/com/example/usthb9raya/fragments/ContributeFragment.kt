package com.example.usthb9raya.fragments

import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import com.example.usthb9raya.R
import com.example.usthb9raya.Utils.FirebaseUtil.contributionsRef
import com.example.usthb9raya.Utils.FirebaseUtil.storageRef
import com.example.usthb9raya.Utils.Utils
import com.example.usthb9raya.Utils.Utils.getFileNameFromUri
import com.example.usthb9raya.Utils.Utils.isEditTextEmpty
import com.example.usthb9raya.Utils.Utils.isTextViewEmpty
import com.example.usthb9raya.Utils.Utils.isValidEmail
import com.example.usthb9raya.Utils.Utils.multiChoiceDialog
import com.example.usthb9raya.Utils.Utils.singleChoiceDialog
import com.example.usthb9raya.dataClass.Contribution
import com.example.usthb9raya.databinding.FragmentContributeBinding
import com.example.usthb9raya.viewModels.ContributionViewModel
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
    private lateinit var youtubeLink: EditText
    private var fileUris: MutableList<Uri> = mutableListOf()
    private var fileNames: MutableList<String> = mutableListOf()
    private lateinit var progressBar: ProgressBar
    private lateinit var contributeButt: AppCompatButton
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var addFileButt: AppCompatButton
    private lateinit var viewModel: ContributionViewModel
    private val pendingActions = mutableListOf<() -> Unit>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContributeBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[ContributionViewModel::class.java]

        progressBar = binding.progressBar

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            progressBar.progress = progress ?: 0
        }

        viewModel.progressBarVisibility.observe(viewLifecycleOwner) { visibility ->
            progressBar.visibility = visibility ?: View.GONE
        }

        viewModel.contributeButtonVisibility.observe(viewLifecycleOwner) { visibility ->
            contributeButt.visibility = visibility ?: View.VISIBLE
        }

        viewModel.addFileButtonText.observe(viewLifecycleOwner) { text ->
            addFileButt.text = text ?: getString(R.string.ajouter_un_fichier)
        }

        viewModel.addFileButtonVisibility.observe(viewLifecycleOwner) { visibility ->
            addFileButt.visibility = visibility ?: View.VISIBLE
        }

        viewModel.facultyText.observe(viewLifecycleOwner) { facultyText ->
            faculty.text = facultyText ?: "faculty"
        }

        viewModel.typeText.observe(viewLifecycleOwner) { typeText ->
            type.text = typeText ?: "type"
        }

        viewModel.enableAddFileButton.observe(viewLifecycleOwner) { isEnable ->
            addFileButt.isEnabled = isEnable
        }

        viewModel.youtubeLinkEditTextVisibility.observe(viewLifecycleOwner) { visibility ->
            youtubeLink.visibility = visibility ?: View.GONE
        }

        addFileButt = binding.buttAddFile
        getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if(uris.isNotEmpty()) {
                for(uri in uris) {
                    fileUris.add(uri)
                    fileNames.add(getFileNameFromUri(requireContext(), uri))
                }
                viewModel.setAddFileButtonText(getString(R.string.changer_le_fichier))
            }
        }

        Log.e("FileUri", fileUris.toString())
        fullName = binding.fullName
        email = binding.email
        faculty = binding.faculty
        module = binding.module
        type = binding.type
        youtubeLink = binding.youtubeLink
        comment = binding.comment


        contributeButt = binding.buttContribute
        contributeButt.setOnClickListener {

            if((isEditTextEmpty(fullName) ||
                !isValidEmail(email) ||
                isTextViewEmpty(faculty) ||
                isEditTextEmpty(module) ||
                isTextViewEmpty(type))
                ) {

                return@setOnClickListener
            }
            if (youtubeLink.visibility == View.VISIBLE && isEditTextEmpty(youtubeLink)) {
                    return@setOnClickListener
            }

            handleSelectedFile(fileUris)
        }

        addFileButt.setOnClickListener {
            openFilePicker()
        }

        val facultyList = resources.getStringArray(R.array.faculties)
        faculty.setOnClickListener {
            singleChoiceDialog(requireContext(), facultyList, "Faculté", faculty) {
                viewModel.setFacultyText(faculty.text.toString())
            }
        }


        val typeList = resources.getStringArray(R.array.type)
        type.setOnClickListener {
            multiChoiceDialog(requireContext(), typeList, "Type", type) { selectedOptionsContainYoutubeLink, youtubeLinkIsTheOnlySelectedOption ->
                if(youtubeLinkIsTheOnlySelectedOption) {
                    viewModel.setAddFileButtonVisibility(View.GONE)
                } else {
                    viewModel.setAddFileButtonVisibility(View.VISIBLE)
                }

                if(selectedOptionsContainYoutubeLink) {
                    viewModel.setYoutubeLinkEditTextVisibility(View.VISIBLE)
                } else {
                    viewModel.setYoutubeLinkEditTextVisibility(View.GONE)
                }
                viewModel.setTypeText(type.text.toString())
            }
        }

    }

    override fun onResume() {
        super.onResume()

        // Execute all pending actions
        pendingActions.forEach { it.invoke() }
        pendingActions.clear() // Clear the queue once actions are executed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFilePicker() {
        fileUris = mutableListOf()
        fileNames = mutableListOf()
        getContent.launch("*/*")
    }

    private fun handleSelectedFile(uris: List<Uri>) {

        if(type.text.toString() == getString(R.string.lien_youtube)) {
            val contributionId = System.currentTimeMillis().toString()
            saveFileContributionToDatabase(contributionId, youtubeLink = youtubeLink.text.toString())
        } else {
            if (fileUris.isNotEmpty()) {
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
            } else {
                Toast.makeText(requireContext(), "Veuillez sélectionner un fichier.", Toast.LENGTH_SHORT).show()
            }
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
        viewModel.setContributeButtonVisibility(View.GONE)
        viewModel.enableAddFileButton(false)
        viewModel.setProgressBarVisibility(View.VISIBLE)
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
                        if (downloadUris.size == fileUris.size) {
                            saveFileContributionToDatabase(contributionId, downloadUris, totalBytes,
                                youtubeLink.text.toString().ifEmpty { null })
                        }
                    }
                }
                .addOnFailureListener { exception ->

                    runWhenFragmentIsAttached {
                        Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }

                    viewModel.setContributeButtonVisibility(View.VISIBLE)
                    viewModel.enableAddFileButton(true)
                    viewModel.setProgressBarVisibility(View.GONE)
                    viewModel.setProgress(0)
                    viewModel.setAddFileButtonText(getString(R.string.ajouter_un_fichier))
                }
        }
    }

    private fun updateProgressBar(totalBytesTransferred: Long, totalBytes: Long) {
        val progress = (100.0 * totalBytesTransferred / totalBytes).toInt()
        viewModel.setProgress(progress)
    }

    private fun saveFileContributionToDatabase(contributionId: String, downloadUris: List<String>? = null, fileSizeInBytes: Long? = null, youtubeLink: String? = null) {

        val contribution = Contribution(contributionId,
            fullName.text.toString(),
            email.text.toString(),
            faculty.text.toString(),
            module.text.toString(),
            type.text.toString(),
            comment.text.toString().ifEmpty { null },
            downloadUris,
            if (downloadUris == null) null else fileNames,
            fileSizeInBytes,
            youtubeLink
        )

        contributionsRef.child(contribution.contributionId).setValue(contribution).addOnCompleteListener { task ->


            if (task.isSuccessful) {
                runWhenFragmentIsAttached {
                    Utils.alertDialog(requireContext(), "Merci pour votre contribution", "Vous recevrez un email lorsque votre contribution sera confirmée.",
                        "Ok", null, {
                            reset()
                        }, null)
                }
                Log.e("ContributeFragment", "saving contribution to database")
            } else {
                Log.e("ContributeFragment", "Error saving contribution to database: ${task.exception}")
                runWhenFragmentIsAttached {
                    Toast.makeText(requireContext(), "Error saving contribution to database", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun reset() {
        faculty.hint = "Faculty"
                faculty.text = ""
                module.hint = "Module"
                module.text.clear()
                type.hint = "Type"
                type.text = ""
        youtubeLink.hint = "Lien Youtube"
        youtubeLink.text.clear()
                comment.hint = "Comment"
                comment.text.clear()
                fileUris = mutableListOf()
                fileNames = mutableListOf()
                viewModel.setAddFileButtonText(getString(R.string.ajouter_un_fichier))
                viewModel.setProgressBarVisibility(View.GONE)
                viewModel.setProgress(0)
                viewModel.setContributeButtonVisibility(View.VISIBLE)
        viewModel.enableAddFileButton(true)
    }

    private fun runWhenFragmentIsAttached(action: () -> Unit) {
        if (isAdded && isResumed) {
            // Fragment is attached and resumed, execute the action immediately
            action()
        } else {
            // Fragment is not attached, add to the queue for later
            pendingActions.add(action)
        }
    }
}

