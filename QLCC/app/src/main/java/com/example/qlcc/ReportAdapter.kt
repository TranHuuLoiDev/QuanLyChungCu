package com.example.qlcc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private val reports: List<Report>) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

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
            holder.status.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
        } else {
            holder.status.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        }
    }

    override fun getItemCount() = reports.size
}