package com.example.usthb9raya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.usthb9raya.databinding.FragmentContributeBinding

class ContributeFragment : Fragment(R.layout.fragment_contribute) {
    private var _binding: FragmentContributeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContributeBinding.bind(view)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}