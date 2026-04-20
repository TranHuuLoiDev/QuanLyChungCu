package com.example.qlcc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class InvoiceAdapter(
    private val invoiceList: List<Invoice>,
    private val onInvoiceClick: (Invoice) -> Unit // Bắt sự kiện khi Admin bấm vào
) : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvInvoiceTitle)
        val tvRoom: TextView = view.findViewById(R.id.tvInvoiceRoom)
        val tvAmount: TextView = view.findViewById(R.id.tvInvoiceAmount)
        val tvDate: TextView = view.findViewById(R.id.tvInvoiceDate)
        val tvStatus: TextView = view.findViewById(R.id.tvInvoiceStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invoice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoice = invoiceList[position]

        holder.tvTitle.text = invoice.invoiceTitle
        holder.tvRoom.text = "Phòng: ${invoice.roomId}"
        holder.tvDate.text = "Ngày tạo: ${invoice.createdDate}"

        // Định dạng tiền tệ: 150000 -> 150,000 VNĐ
        val formatter = DecimalFormat("#,###")
        holder.tvAmount.text = "Số tiền: ${formatter.format(invoice.amount)} VNĐ"

        holder.tvStatus.text = invoice.status

        // Đổi màu nền và màu chữ tùy trạng thái
        if (invoice.status == "Đã thanh toán") {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")) // Xanh lá
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#C62828")) // Đỏ
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"))
        }

        holder.itemView.setOnClickListener {
            onInvoiceClick(invoice)
        }
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }
}