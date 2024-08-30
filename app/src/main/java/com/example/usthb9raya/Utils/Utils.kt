package com.example.usthb9raya.Utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.text.TextUtils.replace
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment

object Utils {
    fun openDriveLink(context: Context, url: String?) {
        if (url != null) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        } else {
            Log.e("Utils", "URL is null")
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


}