package com.example.usthb9raya

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat.recreate
import com.example.usthb9raya.Utils.FirebaseUtil.contributionsRef
import com.example.usthb9raya.Utils.FirebaseUtil.storageRef
import com.example.usthb9raya.Utils.Utils
import com.example.usthb9raya.Utils.Utils.getMimeType
import com.example.usthb9raya.Utils.Utils.isEditTextEmpty
import com.example.usthb9raya.Utils.Utils.isTextViewEmpty
import com.example.usthb9raya.Utils.Utils.isValidEmail
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
    private var fileUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarCancelButtContainer: LinearLayout
    private lateinit var contributeButt: AppCompatButton
    private lateinit var cancelButt: ImageButton
    private var uploadTask: UploadTask? = null
    private var mimeType: String? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            fileUri = uri
            mimeType = getMimeType(requireContext(), uri)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContributeBinding.bind(view)

        progressBar = binding.progressBar
        fullName = binding.fullName
        email = binding.email
        faculty = binding.faculty
        module = binding.module
        type = binding.type
        comment = binding.comment
        progressBarCancelButtContainer = binding.progressBarCancelButtContainer

        contributeButt = binding.buttContribute
        contributeButt.setOnClickListener {
            if(!isEditTextEmpty(fullName) &&
                isValidEmail(email) &&
                !isTextViewEmpty(faculty) &&
                !isEditTextEmpty(module) &&
                !isTextViewEmpty(type)
                ) {
                val uri = fileUri
                if (uri != null) {
                    handleSelectedFile(uri)
                    Toast.makeText(requireContext(), "Contribute", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please select a file", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttAddFile.setOnClickListener {
            openFilePicker()
        }

        val facultyList = resources.getStringArray(R.array.faculties)
        faculty.setOnClickListener {
            alertDialog(facultyList, "Faculty", faculty)
        }

        val typeList = resources.getStringArray(R.array.type)
        type.setOnClickListener {
            alertDialog(typeList, "Type", type)
        }

        cancelButt = binding.buttCancel
        cancelButt.setOnClickListener {
            uploadTask?.pause()
            Utils.alertDialog(requireContext(), "Cancel", "Are you sure you want to cancel?", "Yes", "No",
                {
                    uploadTask?.cancel()
                    progressBarCancelButtContainer.visibility = View.GONE
                    progressBar.progress = 0
                    contributeButt.visibility = View.VISIBLE
                },
                {
                    it.dismiss()
                    uploadTask?.resume()
                })

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun alertDialog(options: Array<String>, title: String, textView: TextView) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        builder.setSingleChoiceItems(options, -1) { dialogInterface, which ->
            textView.setText(options[which])
            dialogInterface.dismiss()
        }

        builder.show()
    }

    private fun openFilePicker() {
        getContent.launch("*/*")
    }

    private fun handleSelectedFile(uri: Uri) {

        try {
            val fileSize = getFileSize(uri)

            val maxFileSize = 200 * 1024 * 1024

            if (fileSize > maxFileSize) {
                Toast.makeText(requireContext(), "File is too large (more than 200MB)", Toast.LENGTH_SHORT).show()
                return
            }

            val contributionId = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val fileReference = storageRef.child("uploads").child(contributionId)

            uploadTask = fileReference.putFile(uri)

            contributeButt.visibility = View.GONE
            progressBarCancelButtContainer.visibility = View.VISIBLE

            uploadTask?.addOnProgressListener { snapshot ->

                val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toInt()

                progressBar.progress = progress
            }
                ?.addOnSuccessListener { _ ->
                fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    val contribution = Contribution(fullName.text.toString(),
                        email.text.toString(),
                        faculty.text.toString(),
                        module.text.toString(),
                        type.text.toString(),
                        comment.text.toString(),
                        downloadUri.toString(),
                        contributionId,
                        mimeType ?: "*/*"
                        )
                    saveFileContributionToDatabase(contribution)
                }
            }?.addOnFailureListener { exception ->
                    if (exception !is StorageException || exception.message != "User cancelled the upload") {
                        Toast.makeText(requireContext(), "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    contributeButt.visibility = View.VISIBLE
                    progressBarCancelButtContainer.visibility = View.GONE
                    progressBar.progress = 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to open file", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getFileSize(uri: Uri): Long {
        var fileSize: Long = 0
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {

                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    fileSize = it.getLong(sizeIndex)
                }
            }
        }
        return fileSize
    }

    private fun saveFileContributionToDatabase(contribution: Contribution) {
        contributionsRef.child(contribution.contributionId).setValue(contribution).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Utils.alertDialog(requireContext(), "Thanks for the contribution", "You will receive a notification when your contribution is confirmed.",
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
        fileUri = null
        progressBarCancelButtContainer.visibility = View.GONE
        progressBar.progress = 0
        contributeButt.visibility = View.VISIBLE
    }
}