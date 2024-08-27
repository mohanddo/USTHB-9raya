package com.example.usthb9raya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.usthb9raya.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}