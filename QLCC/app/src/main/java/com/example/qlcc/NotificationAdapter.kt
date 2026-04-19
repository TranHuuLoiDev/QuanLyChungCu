package com.example.qlcc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 1. THÊM 2 BIẾN SỰ KIỆN CLICK VÀO ĐÂY
class NotificationAdapter(
    private val notifications: List<NotificationModel>,
    private val onItemClick: ((NotificationModel) -> Unit)? = null,
    private val onItemLongClick: ((NotificationModel) -> Unit)? = null
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

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

        // Kiểm tra loại thông báo để đổi màu chữ
        if (noti.type == "Khẩn cấp") {
            holder.typeAndDate.text = "🔴 Khẩn cấp • ${noti.createdAt}"
            holder.typeAndDate.setTextColor(Color.parseColor("#D32F2F")) // Màu đỏ
        } else {
            holder.typeAndDate.text = "🔵 Thông thường • ${noti.createdAt}"
            holder.typeAndDate.setTextColor(Color.parseColor("#1976D2")) // Màu xanh
        }

        // ==========================================
        // 2. XỬ LÝ SỰ KIỆN KHI CLICK / NHẤN GIỮ
        // ==========================================

        // Click bình thường (Có thể dùng để mở trang Sửa)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(noti)
        }

        // Nhấn giữ (Long Click) để hiện Popup Menu (Sửa / Xóa)
        holder.itemView.setOnLongClickListener {
            if (onItemLongClick != null) {
                // Tạo một Menu nhỏ ngay tại vị trí nhấn
                val popup = PopupMenu(holder.itemView.context, holder.itemView)
                popup.menu.add("Chỉnh sửa")
                popup.menu.add("Xóa thông báo")

                popup.setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "Chỉnh sửa" -> {
                            // Gọi sự kiện Click bình thường để xử lý Sửa
                            onItemClick?.invoke(noti)
                        }
                        "Xóa thông báo" -> {
                            // Gọi sự kiện LongClick để xử lý Xóa
                            onItemLongClick.invoke(noti)
                        }
                    }
                    true
                }
                popup.show()
            }
            true // Trả về true để kết thúc sự kiện nhấn giữ
        }
    }

    override fun getItemCount() = notifications.size
}