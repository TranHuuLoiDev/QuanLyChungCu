package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReportHistoryActivity : AppCompatActivity() {

    // Khai báo biến ở đây để có thể dùng chung cho cả hàm onCreate và onResume
    private lateinit var rvReports: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarHistory)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rvReports = findViewById(R.id.rvReports)
        rvReports.layoutManager = LinearLayoutManager(this)
        dbHelper = DatabaseHelper(this)

        // Lấy ID người dùng hiện tại
        @Suppress("DEPRECATION")
        currentUser = intent.getSerializableExtra("USER_INFO") as? User

        // Xử lý sự kiện nút Tạo Mới
        val btnCreateNewReport = findViewById<Button>(R.id.btnCreateNewReport)
        btnCreateNewReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)

            // ---> ĐÂY CHÍNH LÀ DÒNG BẠN THIẾU <---
            // Phải gửi thông tin người dùng sang trang gửi thì nó mới cho lưu vào Database
            intent.putExtra("USER_INFO", currentUser)

            startActivity(intent)
        }
    }

    // =========================================================
    // HÀM MỚI: Tự động tải lại danh sách khi màn hình này hiện lên
    // =========================================================
    override fun onResume() {
        super.onResume()

        // Mỗi khi màn hình Lịch sử hiện lên (bao gồm cả lúc vừa gửi phản ánh xong và quay lại)
        // Nó sẽ tự động chọc vào Database để lấy danh sách mới nhất
        if (currentUser != null) {
            val list = dbHelper.getReportsByUserId(currentUser!!.userId)
            rvReports.adapter = ReportAdapter(list)
        }
    }
}