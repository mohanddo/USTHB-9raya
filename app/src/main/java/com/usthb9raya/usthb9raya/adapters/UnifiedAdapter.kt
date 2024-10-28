package com.usthb9raya.usthb9raya.adapters

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.usthb9raya.usthb9raya.R
import com.usthb9raya.usthb9raya.Utils.Utils
import com.usthb9raya.usthb9raya.YouTubeActivity
import com.usthb9raya.usthb9raya.dataClass.Faculty
import com.usthb9raya.usthb9raya.dataClass.Module
import com.usthb9raya.usthb9raya.dataClass.SousModule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type


class UnifiedAdapter(
    private val itemList: List<Any>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FACULTY = 0
        private const val TYPE_MODULE = 1
        private const val TYPE_SOUS_MODULE = 2
    }

    private val expandedFacultyState = mutableMapOf<Int, Boolean>()

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is Faculty -> TYPE_FACULTY
            is Module -> TYPE_MODULE
            is SousModule -> TYPE_SOUS_MODULE
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_FACULTY -> FacultyViewHolder(inflater.inflate(R.layout.faculty_row, parent, false))
            TYPE_MODULE -> ModuleViewHolder(inflater.inflate(R.layout.module_row, parent, false))
            TYPE_SOUS_MODULE -> SousModuleViewHolder(inflater.inflate(R.layout.sous_module_row, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FacultyViewHolder -> holder.bind(itemList[position] as Faculty, position)
            is ModuleViewHolder -> holder.bind(itemList[position] as Module)
            is SousModuleViewHolder -> holder.bind(itemList[position] as SousModule)
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class FacultyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slideInAnimation = AnimationUtils.loadLayoutAnimation(itemView.context, R.anim.layout_animation)
        private val slideOutAnimation = AnimationUtils.loadLayoutAnimation(itemView.context, R.anim.layout_animation_out)
        private val facultyName: TextView = itemView.findViewById(R.id.text_view_faculty)
        private val facultyLayout: LinearLayout = itemView.findViewById(R.id.linear_faculty)
        private val modulesRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recycler_view)
        private val arrowIcon: ImageView = itemView.findViewById(R.id.ic_arrow_faculty)

        fun bind(faculty: Faculty, position: Int) {
            facultyName.text = faculty.faculty_name
            facultyLayout.setOnClickListener {
                toggleExpand(position)
            }

            modulesRecyclerView.layoutManager = LinearLayoutManager(context)
            modulesRecyclerView.adapter = UnifiedAdapter(faculty.modules, context)

            val isExpanded = expandedFacultyState[position] == true
            if (isExpanded) {
                modulesRecyclerView.layoutAnimation = slideInAnimation
                modulesRecyclerView.visibility = View.VISIBLE
                arrowIcon.setImageResource(R.drawable.ic_arrow_down)
                facultyLayout.setBackgroundResource(R.drawable.bck_click_textview)
                modulesRecyclerView.startLayoutAnimation()
            } else {
                modulesRecyclerView.layoutAnimation = slideOutAnimation
                modulesRecyclerView.startLayoutAnimation()
                slideOutAnimation.animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        modulesRecyclerView.visibility = View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                arrowIcon.setImageResource(R.drawable.ic_arrow_right)
                facultyLayout.setBackgroundResource(R.drawable.bck_textview)
            }
        }

        private fun toggleExpand(position: Int) {
            expandedFacultyState[position] = expandedFacultyState[position] != true
            notifyItemChanged(position)
        }
    }





    //faculty.setBackgroundResource(R.drawable.bck_textview)

    inner class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slideInAnimation = AnimationUtils.loadLayoutAnimation(itemView.context, R.anim.layout_animation)
        private val moduleLayout: LinearLayout = itemView.findViewById(R.id.linear_module)
        private val courseLayout: LinearLayout = itemView.findViewById(R.id.linear_course)
        private val tpLayout: LinearLayout = itemView.findViewById(R.id.linear_tp)
        private val tdLayout: LinearLayout = itemView.findViewById(R.id.linear_td)
        private val examsLayout: LinearLayout = itemView.findViewById(R.id.linear_exams)
        private val othersLayout: LinearLayout = itemView.findViewById(R.id.linear_others)
        private val youtubeLayout: LinearLayout = itemView.findViewById(R.id.linear_youtube)
        private val moduleName: TextView = itemView.findViewById(R.id.text_view_module)
        private val submoduleRecyclerView: RecyclerView = itemView.findViewById(R.id.submodule_recycler_view)
        private val moduleArrow: ImageView = itemView.findViewById(R.id.ic_arrow_module)
        private val moduleHeart: ImageView = itemView.findViewById(R.id.ic_favorite_module)
        private var sousModuleAdapter: UnifiedAdapter? = null

        init {
            moduleLayout.setOnClickListener {
                val module = itemList[adapterPosition] as Module
                val isSubmoduleVisible = submoduleRecyclerView.visibility == View.VISIBLE
                val isDetailsVisible = courseLayout.visibility == View.VISIBLE

                if (module.submodules.isNotEmpty()) {
                    // Toggle submodule RecyclerView
                    toggleSubmoduleRecyclerView(isSubmoduleVisible)
                } else {
                    // Toggle visibility of course, TP, TD, etc.
                    toggleCourseTpTdVisibility(isDetailsVisible)
                }

                // Update arrow and background for expanded/collapsed state
                moduleName.isSelected = !isSubmoduleVisible && !isDetailsVisible
                moduleLayout.setBackgroundResource(
                    if (isSubmoduleVisible || isDetailsVisible) R.drawable.bck_textview else R.drawable.bck_click_textview
                )
                moduleArrow.setImageResource(
                    if (isSubmoduleVisible || isDetailsVisible) R.drawable.ic_arrow_right else R.drawable.ic_arrow_down
                )

                if (sousModuleAdapter == null && module.submodules.isNotEmpty()) {
                    sousModuleAdapter = UnifiedAdapter(module.submodules, context)
                    submoduleRecyclerView.layoutManager = LinearLayoutManager(context)
                    submoduleRecyclerView.adapter = sousModuleAdapter
                }
            }

            courseLayout.setOnClickListener { Utils.openDriveLink(context, (itemList[adapterPosition] as Module).course_link) }
            tpLayout.setOnClickListener { Utils.openDriveLink(context, (itemList[adapterPosition] as Module).tp_link) }
            tdLayout.setOnClickListener { Utils.openDriveLink(context, (itemList[adapterPosition] as Module).td_link) }
            examsLayout.setOnClickListener { Utils.openDriveLink(context, (itemList[adapterPosition] as Module).exams_link) }
            othersLayout.setOnClickListener { Utils.openDriveLink(context, (itemList[adapterPosition] as Module).others_link) }
            youtubeLayout.setOnClickListener {
                val module = itemList[adapterPosition] as Module
                context.startActivity(Intent(context, YouTubeActivity::class.java).apply {
                    putExtra("MODULE_NAME", module.module_name)
                })
            }

            moduleHeart.setOnClickListener {
                val module = itemList[adapterPosition] as Module
                if (isModuleInFavorites(module)) {
                    removeModuleFromFavorites(module)
                    moduleHeart.setImageResource(R.drawable.ic_heart)
                } else {
                    addModuleToFavorites(module)
                    moduleHeart.setImageResource(R.drawable.ic_heart_click)
                }
            }
        }

        fun bind(module: Module) {
            moduleName.text = module.module_name
            val isFavorite = isModuleInFavorites(module)
            updateHeartIcon(isFavorite)
        }

        private fun toggleSubmoduleRecyclerView(isVisible: Boolean) {
            if (isVisible) {

                        submoduleRecyclerView.visibility = View.GONE



            } else {

                submoduleRecyclerView.visibility = View.VISIBLE
                submoduleRecyclerView.layoutAnimation = slideInAnimation
                submoduleRecyclerView.startLayoutAnimation()
            }
        }

        private fun toggleCourseTpTdVisibility(isVisible: Boolean) {
            val visibility = if (isVisible) View.GONE else View.VISIBLE
            courseLayout.visibility = visibility
            tpLayout.visibility = visibility
            tdLayout.visibility = visibility
            examsLayout.visibility = visibility
            othersLayout.visibility = visibility
            youtubeLayout.visibility = visibility
        }

        private fun isModuleInFavorites(module: Module): Boolean {
            val file = File(context.filesDir, "favorites.json")
            if (!file.exists()) return false
            val existingJson = file.readText()
            val type: Type = object : TypeToken<MutableList<Module>>() {}.type
            val favoriteModules: MutableList<Module> = try {
                Gson().fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
            return favoriteModules.any { it.module_name == module.module_name }
        }

        private fun updateHeartIcon(isFavorite: Boolean) {
            moduleHeart.setImageResource(
                if (isFavorite) R.drawable.ic_heart_click else R.drawable.ic_heart
            )
        }

        private fun addModuleToFavorites(module: Module) {
            val file = File(context.filesDir, "favorites.json")
            val existingJson = if (file.exists()) file.readText() else "[]"
            val type: Type = object : TypeToken<MutableList<Module>>() {}.type
            val gson = Gson()
            val favoriteModules: MutableList<Module> = try {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
            if (favoriteModules.none { it.module_name == module.module_name }) {
                favoriteModules.add(module)
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
            val type: Type = object : TypeToken<MutableList<Module>>() {}.type
            val gson = Gson()
            val favoriteModules: MutableList<Module> = try {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            val updatedModules = favoriteModules.filterNot { it.module_name == module.module_name }
            val updatedJson = gson.toJson(updatedModules)
            try {
                file.writeText(updatedJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private val expandedState = mutableMapOf<Int, Boolean>()


    inner class SousModuleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val sousModuleName: TextView =
            itemView.findViewById(R.id.text_view_sous_module)
        private val sousModule: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module)
        private val sousModuleTp: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module_tp)
        private val sousModuleTd: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module_td)
        private val sousModuleCourse: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module_course)
        private val sousModuleExams: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module_exams)
        private val sousModuleOthers: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module_others)
        private val sousModuleYoutube: LinearLayout =
            itemView.findViewById(R.id.linear_sous_module_youtube)
        private val sousModuleArrow: ImageView =
            itemView.findViewById(R.id.ic_arrow_sous_module)
        private val sousModuleHeart: ImageView =
            itemView.findViewById(R.id.ic_favorite_sous_module)

        init {
            sousModule.setOnClickListener {
                val isExpanded = expandedState[adapterPosition] ?: false
                expandedState[adapterPosition] = !isExpanded
                notifyItemChanged(adapterPosition)
            }

            // Set up click listeners for the other views (unchanged)
            setClickListeners()
        }

        fun bind(sousModule: SousModule) {
            sousModuleName.text = sousModule.sous_module_name
            val isExpanded = expandedState[adapterPosition] ?: false
            sousModuleName.isSelected = isExpanded
            val visibility = if (isExpanded) View.VISIBLE else View.GONE
            val arrowResource =
                if (isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right

            // Directly set visibility without animations
            sousModuleCourse.visibility = visibility
            sousModuleTp.visibility = visibility
            sousModuleTd.visibility = visibility
            sousModuleExams.visibility = visibility
            sousModuleOthers.visibility = visibility
            sousModuleYoutube.visibility = visibility

            this.sousModule.setBackgroundResource(if (isExpanded) R.drawable.bck_click_textview else R.drawable.bck_textview)
            sousModuleArrow.setImageResource(arrowResource)

            val isFavorite = isSousModuleInFavorites(sousModule)
            updateHeartIcon(isFavorite)
        }

        private fun setClickListeners() {
            sousModuleCourse.setOnClickListener {
                Utils.openDriveLink(
                    context,
                    (itemList[adapterPosition] as SousModule).sous_module_course_link
                )
            }
            sousModuleTp.setOnClickListener {
                Utils.openDriveLink(
                    context,
                    (itemList[adapterPosition] as SousModule).sous_module_tp_link
                )
            }
            sousModuleTd.setOnClickListener {
                Utils.openDriveLink(
                    context,
                    (itemList[adapterPosition] as SousModule).sous_module_td_link
                )
            }
            sousModuleExams.setOnClickListener {
                Utils.openDriveLink(
                    context,
                    (itemList[adapterPosition] as SousModule).sous_module_exams_link
                )
            }
            sousModuleOthers.setOnClickListener {
                Utils.openDriveLink(
                    context,
                    (itemList[adapterPosition] as SousModule).sous_module_others_link
                )
            }

            itemView.findViewById<LinearLayout>(R.id.linear_sous_module_youtube)
                .setOnClickListener {
                    val sousModule = itemList[adapterPosition] as SousModule
                    context.startActivity(
                        Intent(
                            context,
                            YouTubeActivity::class.java
                        ).apply {
                            putExtra("MODULE_NAME", sousModule.sous_module_name)
                        })
                }

            sousModuleHeart.setOnClickListener {
                val sousModule = itemList[adapterPosition] as SousModule
                if (isSousModuleInFavorites(sousModule)) {
                    removeSousModuleFromFavorites(sousModule)
                    sousModuleHeart.setImageResource(R.drawable.ic_heart)
                } else {
                    addSousModuleToFavorites(sousModule)
                    sousModuleHeart.setImageResource(R.drawable.ic_heart_click)
                }
            }
        }

        private fun updateHeartIcon(isFavorite: Boolean) {
            sousModuleHeart.setImageResource(
                if (isFavorite) R.drawable.ic_heart_click else R.drawable.ic_heart
            )
        }

        private fun isSousModuleInFavorites(sousModule: SousModule): Boolean {
            // Check if the sousModule is in favorites (unchanged)
            val file = File(context.filesDir, "favorites_submodules.json")
            if (!file.exists()) return false
            val existingJson = file.readText()
            val type: Type = object : TypeToken<MutableList<SousModule>>() {}.type
            val favoriteSousModules: MutableList<SousModule> = try {
                Gson().fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
            return favoriteSousModules.any { it.sous_module_name == sousModule.sous_module_name }
        }

        private fun addSousModuleToFavorites(sousModule: SousModule) {
            // Add sousModule to favorites (unchanged)
            val file = File(context.filesDir, "favorites_submodules.json")
            val existingJson = if (file.exists()) file.readText() else "[]"
            val type: Type = object : TypeToken<MutableList<SousModule>>() {}.type
            val gson = Gson()
            val favoriteSousModules: MutableList<SousModule> = try {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
            if (favoriteSousModules.none { it.sous_module_name == sousModule.sous_module_name }) {
                favoriteSousModules.add(sousModule)
                val updatedJson = gson.toJson(favoriteSousModules)
                try {
                    file.writeText(updatedJson)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun removeSousModuleFromFavorites(sousModule: SousModule) {
            // Remove sousModule from favorites (unchanged)
            val file = File(context.filesDir, "favorites_submodules.json")
            val existingJson = if (file.exists()) file.readText() else "[]"
            val type: Type = object : TypeToken<MutableList<SousModule>>() {}.type
            val gson = Gson()
            val favoriteSousModules: MutableList<SousModule> = try {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            val updatedSousModules =
                favoriteSousModules.filterNot { it.sous_module_name == sousModule.sous_module_name }
            val updatedJson = gson.toJson(updatedSousModules)
            try {
                file.writeText(updatedJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
