package com.example.qlcc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApartmentAdapter(private val apartments: List<Apartment>) :
    RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgApartment: ImageView = view.findViewById(R.id.imgApartment)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_apartment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apt = apartments[position]

        holder.imgApartment.setImageResource(apt.imageRes)
        holder.tvName.text = apt.name                    // Sử dụng name từ property
        holder.tvInfo.text = apt.area ?: "Không có diện tích"
        holder.tvStatus.text = apt.status ?: "Còn trống"
        holder.tvDesc.text = apt.desc ?: "Không có mô tả"
    }
    override fun getItemCount() = apartments.size
}