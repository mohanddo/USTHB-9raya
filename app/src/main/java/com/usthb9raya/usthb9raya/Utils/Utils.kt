package com.usthb9raya.usthb9raya.Utils

import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

object Utils {
    fun openDriveLink(context: Context, url: String?) {
        if (url != null) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        } else {
            Log.e("Utils", "URL is null")
        }
    }

    fun openYouTubeLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.setPackage("com.google.android.youtube")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }

    fun isEditTextEmpty(edittext: EditText) : Boolean {
        val text = edittext.text.toString().trim()
        if (text.isEmpty()) {
            edittext.error = "Please fill out this field"
            edittext.requestFocus()
            return true
        }
        return false
    }

    fun isTextViewEmpty(textView: TextView) : Boolean {
        val text = textView.text.toString().trim()
        if (text.isEmpty()) {
            textView.error = "Please fill out this field"
            textView.requestFocus()
            return true
        }
        return false
    }


    fun isValidEmail(email: EditText): Boolean {
         if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
             email.error = "Please enter a valid email address"
             email.requestFocus()
             return false
         }

        if(email.text.toString().isEmpty()) {
            email.error = "Please fill out this field"
            email.requestFocus()
            return false
        }

        return true
    }

    fun alertDialog(context: Context, title: String, message: String, positiveButtonMessage: String,
                            negativeButtonMessage: String? = null, positiveButtonAction: () -> Unit, negativeButtonAction: ((DialogInterface) -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonMessage) { _, _ ->
            positiveButtonAction.invoke()
        }
        negativeButtonMessage?.let {
            builder.setNegativeButton(it) { dialog, _ ->
                negativeButtonAction?.invoke(dialog)
            }
        }

        builder.show()

    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        return contentResolver.getType(uri)
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }
        return fileName ?: "Unknown File"
    }

    fun multiChoiceDialog(context: Context, options: Array<String>, title: String, textView: TextView, action: (selectedOptionsContainYoutubeLink: Boolean, youtubeLinkIsTheOnlySelectedOption: Boolean) -> Unit) {

        val selectedItems = BooleanArray(options.size)

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)

        builder.setMultiChoiceItems(options, selectedItems) { _, which, isChecked ->
            selectedItems[which] = isChecked
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            val selectedOptions = options.filterIndexed { index, _ -> selectedItems[index] }.joinToString(", ")

            textView.text = selectedOptions
            dialog.dismiss()
            action.invoke(selectedOptions.contains("Lien Youtube"), selectedOptions == "Lien Youtube")
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    fun singleChoiceDialog(context: Context, options: Array<String>, title: String, textView: TextView, action: () -> Unit) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)

        builder.setSingleChoiceItems(options, -1) { dialogInterface, which ->
            textView.setText(options[which])
            dialogInterface.dismiss()
            action.invoke()
        }

        builder.show()
    }
}