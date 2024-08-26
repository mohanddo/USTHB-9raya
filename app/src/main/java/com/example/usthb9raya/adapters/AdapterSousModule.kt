package com.example.usthb9raya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.dataClass.DataClassSousModule
import com.example.usthb9raya.R


class AdapterSousModule (private var sousModule: List<DataClassSousModule>) :
    RecyclerView.Adapter<AdapterSousModule.MyViewHolder>() {



        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val sous_module: LinearLayout = itemView.findViewById(R.id.linear_sous_module)
            val sous_module_course: LinearLayout = itemView.findViewById(R.id.linear_sous_module_course)
            val sous_module_tp: LinearLayout = itemView.findViewById(R.id.linear_sous_module_tp)
            val sous_module_td: LinearLayout = itemView.findViewById(R.id.linear_sous_module_td)
            val sous_module_module_name: TextView = itemView.findViewById(R.id.text_view_sous_module)
            val sous_module_course_name: TextView = itemView.findViewById(R.id.text_view_sous_module_course)
            val sous_module_tp_name: TextView = itemView.findViewById(R.id.text_view_sous_module_tp)
            val sous_module_td_name: TextView = itemView.findViewById(R.id.text_view_sous_module_td)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sous_module_row, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = sousModule[position]

            // Set text for views
            holder.sous_module_module_name.text = item.sous_module_name
            holder.sous_module_course_name.text = item.sous_module_course_name
            holder.sous_module_tp_name.text = item.sous_module_tp_name
            holder.sous_module_td_name.text = item.sous_module_td_name

            // Initialize visibility states
            holder.sous_module.visibility = View.VISIBLE
            holder.sous_module_course.visibility = View.GONE
            holder.sous_module_tp.visibility = View.GONE
            holder.sous_module_td.visibility = View.GONE


            // Handle module click to toggle course, tp, and td visibility
            holder.sous_module.setOnClickListener {
                if (holder.sous_module_course.visibility == View.GONE) {
                    holder.sous_module_course.visibility = View.VISIBLE
                    holder.sous_module_tp.visibility = View.VISIBLE
                    holder.sous_module_td.visibility = View.VISIBLE
                } else {
                    holder.sous_module_course.visibility = View.GONE
                    holder.sous_module_tp.visibility = View.GONE
                    holder.sous_module_td.visibility = View.GONE
                }
            }
        }

        override fun getItemCount(): Int {
            return sousModule.size
        }

        fun updateSousModules(newSousModules: List<DataClassSousModule>) {
            sousModule = newSousModules
            notifyDataSetChanged()
        }
}