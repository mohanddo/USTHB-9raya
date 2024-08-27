package com.example.usthb9raya.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usthb9raya.dataClass.DataClassFaculty
import com.example.usthb9raya.R

class AdapterFaculty(
    private val faculties: List<DataClassFaculty>,
    private val context: Context
) : RecyclerView.Adapter<AdapterFaculty.FacultyViewHolder>() {

    inner class FacultyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val faculty_name: TextView = itemView.findViewById(R.id.text_view_faculty)
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recycler_view)
        val faculty: LinearLayout = itemView.findViewById(R.id.linear_faculty)

        private var moduleAdapter: AdapterModule? = null

        init {
            faculty.setOnClickListener {
                val isVisible = innerRecyclerView.visibility == View.VISIBLE
                if (isVisible) {
                    innerRecyclerView.visibility = View.GONE
                    faculty.setBackgroundResource(R.drawable.bck_textview)
                } else {
                    innerRecyclerView.visibility = View.VISIBLE
                    faculty.setBackgroundResource(R.drawable.bck_click_textview)
                    if (moduleAdapter == null) {
                        moduleAdapter = AdapterModule(emptyList(), context)
                        innerRecyclerView.layoutManager = LinearLayoutManager(context)
                        innerRecyclerView.adapter = moduleAdapter
                    }
                }
            }
        }

        fun bind(faculty: DataClassFaculty) {
            if (moduleAdapter == null) {
                moduleAdapter = AdapterModule(faculty.modules, context)
                innerRecyclerView.layoutManager = LinearLayoutManager(context)
                innerRecyclerView.adapter = moduleAdapter
            } else {
                moduleAdapter?.updateModules(faculty.modules)
            }
            faculty_name.text = faculty.faculty_name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.faculty_row, parent, false)
        return FacultyViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        holder.bind(faculties[position])
    }

    override fun getItemCount(): Int = faculties.size
}


