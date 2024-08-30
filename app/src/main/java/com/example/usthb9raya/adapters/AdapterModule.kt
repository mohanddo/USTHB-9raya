package com.example.usthb9raya.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.R
import com.example.usthb9raya.Utils.Utils
import com.example.usthb9raya.dataClass.Module
import com.example.usthb9raya.dataClass.FavoriteModule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class AdapterModule(
    private var itemList: List<Module>,
    private val context: Context
) : RecyclerView.Adapter<AdapterModule.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val module: LinearLayout = itemView.findViewById(R.id.linear_module)
        val course: LinearLayout = itemView.findViewById(R.id.linear_course)
        val tp: LinearLayout = itemView.findViewById(R.id.linear_tp)
        val td: LinearLayout = itemView.findViewById(R.id.linear_td)
        val exams: LinearLayout = itemView.findViewById(R.id.linear_exams)
        val others: LinearLayout = itemView.findViewById(R.id.linear_others)
        val module_name: TextView = itemView.findViewById(R.id.text_view_module)
        val submodule: RecyclerView = itemView.findViewById(R.id.submodule_recycler_view)
        val module_arrow: ImageView = itemView.findViewById(R.id.ic_arrow_module)
        val module_heart: ImageView = itemView.findViewById(R.id.ic_favorite_module)

        private val sousModuleAdapter = AdapterSousModule(emptyList(), context)

        init {
            // Configure module click listener
            module.setOnClickListener {
                val hasSubmodules = itemList[adapterPosition].submodules.isNotEmpty()
                if (hasSubmodules) {
                    // Toggle the visibility of the submodule RecyclerView
                    val isVisible = submodule.visibility == View.VISIBLE
                    if (isVisible) {
                        submodule.visibility = View.GONE
                        module.setBackgroundResource(R.drawable.bck_textview)
                        module_arrow.setImageResource(R.drawable.ic_arrow_right)
                    } else {
                        submodule.visibility = View.VISIBLE
                        module.setBackgroundResource(R.drawable.bck_click_textview)
                        module_arrow.setImageResource(R.drawable.ic_arrow_down)
                        if (submodule.adapter == null) {
                            submodule.layoutManager = LinearLayoutManager(context)
                            submodule.adapter = sousModuleAdapter
                        }
                        sousModuleAdapter.updateSousModules(itemList[adapterPosition].submodules)
                    }
                } else {
                    toggleCourseTpTdVisibility()
                }
            }

            // Configure heart click listener
            module_heart.setOnClickListener {
                val module = itemList[adapterPosition]
                if (isModuleInFavorites(module)) {
                    removeModuleFromFavorites(module)
                    module_heart.setImageResource(R.drawable.ic_heart)
                } else {
                    addModuleToFavorites(module)
                    module_heart.setImageResource(R.drawable.ic_heart_click)
                }
            }
        }

        fun bind(module: Module) {
            module_name.text = module.module_name

            // Initialize the heart icon state based on whether the module is in favorites
            val isFavorite = isModuleInFavorites(module)
            updateHeartIcon(isFavorite)

            // Set click listeners for course, tp, td, exams, and others
            course.setOnClickListener {
                Utils.openDriveLink(context, module.course_link)
            }
            tp.setOnClickListener {
                Utils.openDriveLink(context, module.tp_link)
            }
            td.setOnClickListener {
                Utils.openDriveLink(context, module.td_link)
            }
            exams.setOnClickListener {
                Utils.openDriveLink(context, module.exams_link)
            }
            others.setOnClickListener {
                Utils.openDriveLink(context, module.others_link)
            }
        }

        private fun toggleCourseTpTdVisibility() {
            val isVisible = course.visibility == View.VISIBLE
            course.visibility = if (isVisible) View.GONE else View.VISIBLE
            tp.visibility = if (isVisible) View.GONE else View.VISIBLE
            td.visibility = if (isVisible) View.GONE else View.VISIBLE
            exams.visibility = if (isVisible) View.GONE else View.VISIBLE
            others.visibility = if (isVisible) View.GONE else View.VISIBLE
            val bckSet = if (isVisible) R.drawable.bck_textview else R.drawable.bck_click_textview
            module.setBackgroundResource(bckSet)
            val bckImage = if (isVisible) R.drawable.ic_arrow_right else R.drawable.ic_arrow_down
            module_arrow.setImageResource(bckImage)
        }

        private fun isModuleInFavorites(module: Module): Boolean {
            val file = File(context.filesDir, "favorites.json")
            if (!file.exists()) return false

            val existingJson = file.readText()
            val type: Type = object : TypeToken<MutableList<FavoriteModule>>() {}.type
            val favoriteModules: MutableList<FavoriteModule> = try {
                Gson().fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

            return favoriteModules.any { it.module.module_name == module.module_name }
        }

        private fun updateHeartIcon(isFavorite: Boolean) {
            module_heart.setImageResource(
                if (isFavorite) R.drawable.ic_heart_click else R.drawable.ic_heart
            )
        }

        private fun addModuleToFavorites(module: Module) {
            val file = File(context.filesDir, "favorites.json")

            val existingJson = if (file.exists()) file.readText() else "[]"
            val type: Type = object : TypeToken<MutableList<FavoriteModule>>() {}.type
            val gson = Gson()

            val favoriteModules: MutableList<FavoriteModule> = try {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }

            if (favoriteModules.none { it.module.module_name == module.module_name }) {
                val newFavoriteModule = FavoriteModule(module)
                favoriteModules.add(newFavoriteModule)

                val updatedJson = gson.toJson(favoriteModules)
                try {
                    file.writeText(updatedJson)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun removeModuleFromFavorites(module: Module) {
            val file = File(context.filesDir, "favorites.json")

            val existingJson = if (file.exists()) file.readText() else "[]"
            val type: Type = object : TypeToken<MutableList<FavoriteModule>>() {}.type
            val gson = Gson()

            val favoriteModules: MutableList<FavoriteModule> = try {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }

            favoriteModules.removeAll { it.module.module_name == module.module_name }

            val updatedJson = gson.toJson(favoriteModules)
            try {
                file.writeText(updatedJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.module_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun updateModules(newModules: List<Module>) {
        itemList = newModules
        notifyDataSetChanged()
    }
}









