package com.example.usthb9raya

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.adapters.AdapterFaculty
import com.example.usthb9raya.dataClass.DataClassFaculty
import com.example.usthb9raya.dataClass.DataClassModule
import com.example.usthb9raya.dataClass.DataClassSousModule
import com.example.usthb9raya.databinding.FragmentContributeBinding
import com.example.usthb9raya.databinding.FragmentHomeBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import java.io.InputStream

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