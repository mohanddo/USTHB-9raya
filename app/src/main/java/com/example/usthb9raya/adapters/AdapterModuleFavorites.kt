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
import com.example.usthb9raya.dataClass.FavoriteModule
import com.example.usthb9raya.dataClass.Module
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class AdapterModuleFavorites(
    private var itemList: List<Module>,
    private val context: Context
) : RecyclerView.Adapter<AdapterModuleFavorites.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val module: LinearLayout = itemView.findViewById(R.id.linear_module)
        private val course: LinearLayout = itemView.findViewById(R.id.linear_course)
        private val tp: LinearLayout = itemView.findViewById(R.id.linear_tp)
        private val td: LinearLayout = itemView.findViewById(R.id.linear_td)
        private val exams: LinearLayout = itemView.findViewById(R.id.linear_exams)
        private val others: LinearLayout = itemView.findViewById(R.id.linear_others)
        private val module_name: TextView = itemView.findViewById(R.id.text_view_module)
        private val submodule: RecyclerView = itemView.findViewById(R.id.submodule_recycler_view)
        private val module_arrow: ImageView = itemView.findViewById(R.id.ic_arrow_module)
        private val module_heart : ImageView = itemView.findViewById(R.id.ic_favorite_module)

        private val sousModuleAdapter = AdapterSousModuleFavorites(emptyList(), context)

        init {
            module.setOnClickListener {
                val hasSubmodules = itemList[adapterPosition].submodules.isNotEmpty()
                if (hasSubmodules) {
                    val isVisible = submodule.visibility == View.VISIBLE
                    if (isVisible) {
                        submodule.visibility = View.GONE
                        module.setBackgroundResource(R.drawable.bck_textview)
                        module_arrow.setImageResource(R.drawable.ic_arrow_right) // Changed to right
                    } else {
                        submodule.visibility = View.VISIBLE
                        module.setBackgroundResource(R.drawable.bck_click_textview)
                        module_arrow.setImageResource(R.drawable.ic_arrow_down) // Changed to down
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
        }

        fun bind(module: Module) {
            module_name.text = module.module_name

            val hasSubmodules = module.submodules.isNotEmpty()
            submodule.visibility = if (hasSubmodules) View.GONE else View.VISIBLE
            sousModuleAdapter.updateSousModules(module.submodules)

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




            module_heart.setOnClickListener {

                    removeModuleFromFavorites(itemList[adapterPosition])
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

    private fun removeModuleFromFavorites(module: Module) {
        val file = File(context.filesDir, "favorites.json")

        // Read existing JSON or initialize as an empty array if file does not exist
        val existingJson = if (file.exists()) file.readText() else "[]"

        // Define the type for the list of FavoriteModule
        val type: Type = object : TypeToken<MutableList<FavoriteModule>>() {}.type
        val gson = Gson()

        // Deserialize existing JSON to a list of FavoriteModule, or start with an empty list if deserialization fails
        val favoriteModules: MutableList<FavoriteModule> = try {
            gson.fromJson(existingJson, type) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }

        // Remove the module from the list if it exists
        favoriteModules.removeAll { it.module == module }

        // Serialize the updated list back to JSON and write it to the file
        val updatedJson = gson.toJson(favoriteModules)
        try {
            file.writeText(updatedJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}


