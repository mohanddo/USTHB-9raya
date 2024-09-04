package com.example.usthb9raya.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.R
import com.example.usthb9raya.adapters.UnifiedAdapter
import com.example.usthb9raya.dataClass.Module
import com.example.usthb9raya.dataClass.SousModule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class FavoritesFragment : Fragment() {

    private lateinit var favoritesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        favoritesRecyclerView = view.findViewById(R.id.recyclerViewModulesFavorites)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        val favoriteModules = loadFavoriteModules()
        val favoriteSousModules = loadFavoriteSousModules()


        val combinedFavorites = mutableListOf<Any>()
        combinedFavorites.addAll(favoriteModules)
        combinedFavorites.addAll(favoriteSousModules)


        val adapter = UnifiedAdapter(combinedFavorites, requireContext())
        favoritesRecyclerView.adapter = adapter
    }

    private fun loadFavoriteModules(): List<Module> {
        val file = File(requireContext().filesDir, "favorites.json")
        if (!file.exists()) return emptyList()

        val existingJson = file.readText()
        val type: Type = object : TypeToken<List<Module>>() {}.type

        return try {
            Gson().fromJson(existingJson, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadFavoriteSousModules(): List<SousModule> {
        val file = File(requireContext().filesDir, "favorites_submodules.json")
        if (!file.exists()) return emptyList()

        val existingJson = file.readText()
        val type: Type = object : TypeToken<List<SousModule>>() {}.type

        return try {
            Gson().fromJson(existingJson, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}



