package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CuDanAdapter(
    private val context: Context,
    private val list: ArrayList<CuDan>
) : RecyclerView.Adapter<CuDanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStt: TextView = itemView.findViewById(R.id.tvStt)
        val tvHoTen: TextView = itemView.findViewById(R.id.tvHoTen)
        val tvSoDienThoai: TextView = itemView.findViewById(R.id.tvSoDienThoai)
        val tvSoPhong: TextView = itemView.findViewById(R.id.tvSoPhong)
        val btnSua: Button = itemView.findViewById(R.id.btnSua)
        val btnXoa: Button = itemView.findViewById(R.id.btnXoa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_resident, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cuDan = list[position]

        holder.tvStt.text = (position + 1).toString()
        holder.tvHoTen.text = cuDan.hoTen
        holder.tvSoDienThoai.text = cuDan.soDienThoai
        holder.tvSoPhong.text = cuDan.soPhong

        holder.btnXoa.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                list.removeAt(currentPos)
                notifyItemRemoved(currentPos)
                notifyItemRangeChanged(currentPos, list.size)
                Toast.makeText(context, "Đã xóa cư dân", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnSua.setOnClickListener {
            Toast.makeText(context, "Chức năng sửa đang cập nhật", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = list.size
}
