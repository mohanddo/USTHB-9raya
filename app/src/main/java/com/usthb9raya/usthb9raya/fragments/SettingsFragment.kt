package com.usthb9raya.usthb9raya.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.usthb9raya.usthb9raya.AboutUsthb
import com.usthb9raya.usthb9raya.ContributeRank
import com.usthb9raya.usthb9raya.R
import com.usthb9raya.usthb9raya.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.share.setOnClickListener {
            shareApp()
        }

        binding.Contact.setOnClickListener {
            sendEmail()
        }

        binding.About.setOnClickListener {
            openAbout()
        }

        binding.rank.setOnClickListener {
            openRank()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out USTHB_9raya app: [App Link]")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("usthb9raya@gmail.com"))
            setPackage("com.google.android.gm")
        }
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch(e: Exception) {
            Toast.makeText(context, "Gmail app is not installed", Toast.LENGTH_SHORT).show()
        }


    }


    private fun openRank(){
            val intent = Intent(requireActivity(), ContributeRank::class.java)
            startActivity(intent)
    }

  private  fun openAbout(){
        val intent = Intent(requireActivity(), AboutUsthb::class.java)
        startActivity(intent)
    }
}

