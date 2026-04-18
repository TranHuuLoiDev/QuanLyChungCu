package com.example.qlcc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResidentAdapter(
    private var residentList: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder>() {

    class ResidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStt: TextView = itemView.findViewById(R.id.tvStt)
        val tvHoTen: TextView = itemView.findViewById(R.id.tvHoTen)
        val tvSoDienThoai: TextView = itemView.findViewById(R.id.tvSoDienThoai)
        val tvSoPhong: TextView = itemView.findViewById(R.id.tvSoPhong)
        val btnSua: Button = itemView.findViewById(R.id.btnSua)
        val btnXoa: Button = itemView.findViewById(R.id.btnXoa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resident, parent, false)
        return ResidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        val resident = residentList[position]
        holder.tvStt.text = (position + 1).toString()
        holder.tvHoTen.text = resident.fullName
        holder.tvSoDienThoai.text = resident.phoneNumber
        holder.tvSoPhong.text = resident.roomID

        holder.btnSua.setOnClickListener { onEditClick(resident) }
        holder.btnXoa.setOnClickListener { onDeleteClick(resident) }
    }

    override fun getItemCount(): Int = residentList.size

    fun updateData(newList: List<User>) {
        residentList = newList
        notifyDataSetChanged()
    }
}
