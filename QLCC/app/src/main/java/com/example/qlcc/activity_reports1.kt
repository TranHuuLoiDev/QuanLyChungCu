package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ReportActivity1 : AppCompatActivity() {

    private lateinit var listReports: ListView
    private lateinit var radioStatus: RadioGroup
    private lateinit var tvFilterTitle: TextView
    private lateinit var btnViewReports: Button
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // 1. Ánh xạ các View từ XML (activity_reports.xml)
        btnViewReports = findViewById(R.id.btnViewReports)
        listReports = findViewById(R.id.listReports)
        radioStatus = findViewById(R.id.radioStatus)
        tvFilterTitle = findViewById(R.id.tvFilterTitle)

        // 2. Thiết lập Adapter cho ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        listReports.adapter = adapter

        // 3. Xử lý khi nhấn nút "Xem thông tin phản ánh"
        btnViewReports.setOnClickListener {
            tvFilterTitle.visibility = View.VISIBLE
            radioStatus.visibility = View.VISIBLE
            Toast.makeText(this, "Vui lòng chọn trạng thái để lọc", Toast.LENGTH_SHORT).show()
        }

        // 4. Xử lý khi chọn các RadioButton (Lọc trạng thái)
        radioStatus.setOnCheckedChangeListener { _, checkedId ->
            listReports.visibility = View.VISIBLE
            adapter.clear()

            when (checkedId) {
                R.id.rbPending -> {
                    adapter.add("Rò rỉ nước - Căn A101")
                    adapter.add("Hỏng bóng đèn - Hành lang T3")
                }
                R.id.rbProcessing -> {
                    adapter.add("Sửa thang máy - Block B")
                }
                R.id.rbDone -> {
                    adapter.add("Dọn vệ sinh - Tầng hầm")
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}
