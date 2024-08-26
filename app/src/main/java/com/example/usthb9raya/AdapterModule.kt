package com.example.usthb9raya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdapterModule(private var itemList: List<DataClassModule>) :
    RecyclerView.Adapter<AdapterModule.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val module: LinearLayout = itemView.findViewById(R.id.linear_module)
        val course: LinearLayout = itemView.findViewById(R.id.linear_course)
        val tp: LinearLayout = itemView.findViewById(R.id.linear_tp)
        val td: LinearLayout = itemView.findViewById(R.id.linear_td)
        val module_name: TextView = itemView.findViewById(R.id.text_view_module)
        val submodule: RecyclerView = itemView.findViewById(R.id.submodule_recycler_view)


        // Adapter for submodules
        private val sousModuleAdapter = AdapterSousModule(emptyList())

        init {
            module.setOnClickListener {
                val hasSubmodules = itemList[adapterPosition].submodules.isNotEmpty()
                if (hasSubmodules) {
                    val isVisible = submodule.visibility == View.VISIBLE
                  if (isVisible){
                      submodule.visibility =   View.GONE
                      module.setBackgroundResource(R.drawable.bck_textview)
                    }else{
                      submodule.visibility =  View.VISIBLE
                      module.setBackgroundResource(R.drawable.bck_click_textview)
                    }
                    if (!isVisible) {
                        submodule.layoutManager = LinearLayoutManager(itemView.context)
                        submodule.adapter = sousModuleAdapter
                    }
                } else {

                    toggleCourseTpTdVisibility()
                }
            }
        }

        fun bind(module: DataClassModule) {
            module_name.text = module.module_name


            val hasSubmodules = module.submodules.isNotEmpty()
            submodule.visibility = if (hasSubmodules) View.GONE else View.VISIBLE
            sousModuleAdapter.updateSousModules(module.submodules)
        }

        private fun toggleCourseTpTdVisibility() {
            val isVisible = course.visibility == View.VISIBLE
            course.visibility = if (isVisible) View.GONE else View.VISIBLE
            tp.visibility = if (isVisible) View.GONE else View.VISIBLE
            td.visibility = if (isVisible) View.GONE else View.VISIBLE
            val bckSet = if (isVisible) R.drawable.bck_textview else R.drawable.bck_click_textview
            module.setBackgroundResource(bckSet)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.textview_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun updateModules(newModules: List<DataClassModule>) {
        itemList = newModules
        notifyDataSetChanged()
    }
}

