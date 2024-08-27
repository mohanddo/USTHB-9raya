package com.example.usthb9raya.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.dataClass.SousModule
import com.example.usthb9raya.R
import com.example.usthb9raya.Utils.Utils

class AdapterSousModule(
    private var sousModule: List<SousModule>,
    private val context: Context // Add context if you need it in this adapter
) : RecyclerView.Adapter<AdapterSousModule.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sous_module: LinearLayout = itemView.findViewById(R.id.linear_sous_module)
        val sous_module_course: LinearLayout = itemView.findViewById(R.id.linear_sous_module_course)
        val sous_module_tp: LinearLayout = itemView.findViewById(R.id.linear_sous_module_tp)
        val sous_module_td: LinearLayout = itemView.findViewById(R.id.linear_sous_module_td)
        val sous_module_exams: LinearLayout = itemView.findViewById(R.id.linear_sous_module_exams)
        val sous_module_others: LinearLayout = itemView.findViewById(R.id.linear_sous_module_others)
        val sous_module_module_name: TextView = itemView.findViewById(R.id.text_view_sous_module)
        val sous_module_arrow : ImageView = itemView.findViewById((R.id.ic_arrow_sous_module))
        val sous_module_heart : ImageView = itemView.findViewById(R.id.ic_favorite_sous_module)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sous_module_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = sousModule[position]
        holder.sous_module_module_name.text = item.sous_module_name

        holder.sous_module.visibility = View.VISIBLE
        holder.sous_module_course.visibility = View.GONE
        holder.sous_module_tp.visibility = View.GONE
        holder.sous_module_td.visibility = View.GONE
        holder.sous_module_exams.visibility = View.GONE
        holder.sous_module_others.visibility = View.GONE

        holder.sous_module.setOnClickListener {
            if (holder.sous_module_course.visibility == View.GONE) {
                holder.sous_module_course.visibility = View.VISIBLE
                holder.sous_module_tp.visibility = View.VISIBLE
                holder.sous_module_td.visibility = View.VISIBLE
                holder.sous_module_exams.visibility = View.VISIBLE
                holder.sous_module_others.visibility = View.VISIBLE
                holder.sous_module.setBackgroundResource(R.drawable.bck_click_textview)
                holder.sous_module_arrow.setImageResource(R.drawable.ic_arrow_down)
            } else {
                holder.sous_module_course.visibility = View.GONE
                holder.sous_module_tp.visibility = View.GONE
                holder.sous_module_td.visibility = View.GONE
                holder.sous_module_exams.visibility = View.GONE
                holder.sous_module_others.visibility = View.GONE
                holder.sous_module.setBackgroundResource(R.drawable.bck_textview)
                holder.sous_module_arrow.setImageResource(R.drawable.ic_arrow_right)
            }
        }


        holder.sous_module_course.setOnClickListener {
            Utils.openDriveLink(context, item.sous_module_course_link )
        }
        holder.sous_module_tp.setOnClickListener {
            Utils.openDriveLink(context, item.sous_module_tp_link )
        }

        holder.sous_module_td.setOnClickListener {
            Utils.openDriveLink(context, item.sous_module_td_link )
        }

        holder.sous_module_exams.setOnClickListener {
            Utils.openDriveLink(context, item.sous_module_exams_link )
        }

        holder.sous_module_others.setOnClickListener {
            Utils.openDriveLink(context, item.sous_module_others_link )
        }


    }

    override fun getItemCount(): Int = sousModule.size

        fun updateSousModules(newSousModules: List<SousModule>) {
            sousModule = newSousModules
            notifyDataSetChanged()
        }
}
