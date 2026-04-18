package com.example.qlcc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApartmentAdapter(
    private val aptList: List<Apartment>,
    private val onAptClick: (Apartment) -> Unit // Bắt sự kiện khi Admin bấm vào 1 phòng
) : RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomId: TextView = view.findViewById(R.id.tvRoomId)
        val tvRoomArea: TextView = view.findViewById(R.id.tvRoomArea)
        val tvRoomStatus: TextView = view.findViewById(R.id.tvRoomStatus)
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

        // Đổi màu nền và màu chữ tùy theo trạng thái cho trực quan
        when (apt.roomStatus) {
            "Trống" -> {
                holder.tvRoomStatus.setTextColor(Color.parseColor("#2E7D32")) // Xanh lá
                holder.tvRoomStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
            }
            "Đang ở" -> {
                holder.tvRoomStatus.setTextColor(Color.parseColor("#1565C0")) // Xanh dương
                holder.tvRoomStatus.setBackgroundColor(Color.parseColor("#E3F2FD"))
            }
            else -> { // Ví dụ: Sửa chữa
                holder.tvRoomStatus.setTextColor(Color.parseColor("#E65100")) // Cam
                holder.tvRoomStatus.setBackgroundColor(Color.parseColor("#FFF3E0"))
            }
        }

        // Bắt sự kiện click vào cả cái thẻ (CardView)
        holder.itemView.setOnClickListener {
            onAptClick(apt)
        }
    }

    override fun getItemCount(): Int {
        return aptList.size
    }
}