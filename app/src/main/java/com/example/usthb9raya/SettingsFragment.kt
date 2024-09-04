package com.example.usthb9raya

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.usthb9raya.databinding.FragmentSettingsBinding

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
            type = "vnd.android.cursor.dir/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("akli88mohand@gmail.com"))
        }
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
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

