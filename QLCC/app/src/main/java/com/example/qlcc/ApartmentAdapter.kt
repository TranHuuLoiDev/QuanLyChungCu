package com.example.qlcc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class ApartmentAdapter(
    private val aptList: List<Apartment>,
    private val onAptClick: (Apartment) -> Unit
) : RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomId: TextView = view.findViewById(R.id.tvRoomId)
        val tvRoomArea: TextView = view.findViewById(R.id.tvRoomArea)
        val tvRoomStatus: TextView = view.findViewById(R.id.tvRoomStatus)
        // Bổ sung thêm dòng ánh xạ này
        val tvRoomDesc: TextView = view.findViewById(R.id.tvRoomDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_apartment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apt = aptList[position]

        holder.tvRoomId.text = "Phòng ${apt.roomId}"
        holder.tvRoomArea.text = "Diện tích: ${apt.roomArea} m²"
        holder.tvRoomStatus.text = apt.roomStatus

        // Gắn dữ liệu mô tả (nếu null thì hiện chữ "Chưa có mô tả")
        holder.tvRoomDesc.text = apt.roomDesc ?: "Chưa có mô tả"

        when (apt.roomStatus) {
            "Trống" -> {
                holder.tvRoomStatus.setTextColor("#2E7D32".toColorInt())
                holder.tvRoomStatus.setBackgroundColor("#E8F5E9".toColorInt())
            }
            "Đang ở" -> {
                holder.tvRoomStatus.setTextColor("#1565C0".toColorInt())
                holder.tvRoomStatus.setBackgroundColor("#E3F2FD".toColorInt())
            }
            else -> {
                holder.tvRoomStatus.setTextColor("#E65100".toColorInt())
                holder.tvRoomStatus.setBackgroundColor("#FFF3E0".toColorInt())
            }
        }

        holder.itemView.setOnClickListener {
            onAptClick(apt)
        }
    }

    override fun getItemCount(): Int {
        return aptList.size
    }
}