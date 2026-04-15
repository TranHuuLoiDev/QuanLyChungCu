package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReportHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarHistory)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val rvReports = findViewById<RecyclerView>(R.id.rvReports)
        rvReports.layoutManager = LinearLayoutManager(this)

        val dbHelper = DatabaseHelper(this)

        // Lấy ID người dùng hiện tại
        @Suppress("DEPRECATION")
        val currentUser = intent.getSerializableExtra("USER_INFO") as? User

        if (currentUser != null) {
            // Gọi hàm lấy danh sách phản ánh từ Database
            val list = dbHelper.getReportsByUserId(currentUser.userId)
            rvReports.adapter = ReportAdapter(list)
        }
        // --- THÊM ĐOẠN NÀY ĐỂ KÍCH HOẠT NÚT TẠO MỚI ---
        val btnCreateNewReport = findViewById<android.widget.Button>(R.id.btnCreateNewReport)
        btnCreateNewReport.setOnClickListener {
            // Mở trang Gửi Phản Ánh (cái giao diện đỏ chót bạn vừa làm)
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
    }
}