package com.example.qlcc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notifications: List<NotificationModel>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeAndDate: TextView = view.findViewById(R.id.tvNotiTypeAndDate)
        val title: TextView = view.findViewById(R.id.tvNotiTitle)
        val content: TextView = view.findViewById(R.id.tvNotiContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noti = notifications[position]

        // Gắn dữ liệu
        holder.title.text = noti.title
        holder.content.text = noti.content

        // Kiểm tra loại thông báo để đổi màu chữ cho đẹp
        if (noti.type == "Khẩn cấp") {
            holder.typeAndDate.text = "🔴 Khẩn cấp • ${noti.createdAt}"
            holder.typeAndDate.setTextColor(Color.parseColor("#D32F2F")) // Màu đỏ
        } else {
            holder.typeAndDate.text = "🔵 Thông thường • ${noti.createdAt}"
            holder.typeAndDate.setTextColor(Color.parseColor("#1976D2")) // Màu xanh
        }
    }

    override fun getItemCount() = notifications.size
}