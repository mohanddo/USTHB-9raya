package com.usthb9raya.usthb9raya.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.usthb9raya.usthb9raya.R
import com.usthb9raya.usthb9raya.adapters.UnifiedAdapter
import com.usthb9raya.usthb9raya.dataClass.Module
import com.usthb9raya.usthb9raya.dataClass.SousModule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class FavoritesFragment : Fragment() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesRecyclerView = view.findViewById(R.id.recyclerViewModulesFavorites)
        emptyView = view.findViewById(R.id.empty_view)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val favoriteModules = loadFavoriteModules()
        val favoriteSousModules = loadFavoriteSousModules()

        if (favoriteModules.isEmpty() && favoriteSousModules.isEmpty()) {

            emptyView.visibility = View.VISIBLE
            favoritesRecyclerView.visibility = View.GONE
        } else {

            emptyView.visibility = View.GONE
            favoritesRecyclerView.visibility = View.VISIBLE

            val combinedFavorites = mutableListOf<Any>()
            combinedFavorites.addAll(favoriteModules)
            combinedFavorites.addAll(favoriteSousModules)

            val adapter = UnifiedAdapter(combinedFavorites, requireContext())
            favoritesRecyclerView.adapter = adapter
        }
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




