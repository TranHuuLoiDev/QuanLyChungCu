package com.example.qlcc

import com.example.qlcc.R
import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private var reports: MutableList<Report>) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtReportTitle)
        val content: TextView = view.findViewById(R.id.txtContent)
        val date: TextView = view.findViewById(R.id.txtDate)
        val status: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.title.text = report.title
        holder.content.text = report.content
        holder.date.text = report.createdDate
        holder.status.text = report.status

        // Đổi màu status tùy theo trạng thái
        if (report.status == "Chờ xử lý") {
            holder.status.setBackgroundColor(Color.parseColor("#FF9800"))
        } else {
            holder.status.setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val dbHelper = DatabaseHelper(context)
            val currentPos = holder.adapterPosition

            if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener
            val currentReport = reports[currentPos]

            if (currentReport.status == "Chờ xử lý") {
                // Chú ý: reportId phải trùng với tên biến trong file Report.kt
                val isUpdated = dbHelper.updateReportStatus(currentReport.reportId, "Đã xử lý")

                if (isUpdated) {
                    reports[currentPos] = currentReport.copy(status = "Đã xử lý")
                    notifyItemChanged(currentPos)
                    Toast.makeText(context, "Đã xác nhận xử lý", Toast.LENGTH_SHORT).show()
                }
            }

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Chi tiết phản ánh")
            builder.setMessage(
                "Tiêu đề: ${currentReport.title}\n\n" +
                        "Nội dung: ${currentReport.content}\n\n" +
                        "Ngày gửi: ${currentReport.createdDate}\n\n" +
                        "Trạng thái: Đã xử lý"
            )
            builder.setPositiveButton("Đóng", null)
            builder.show()
        }
    }

    // === ĐÂY LÀ HÀM BẠN ĐANG THIẾU LÀM NÓ BÁO ĐỎ ===
    override fun getItemCount(): Int {
        return reports.size
    }
}
