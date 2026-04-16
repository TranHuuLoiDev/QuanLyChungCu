package com.example.qlcc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // 1. Cài đặt Toolbar màu đỏ và nút Back
        val toolbar = findViewById<Toolbar>(R.id.toolbarReport)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. NHẬN THÔNG TIN CƯ DÂN (Để biết ai là người gửi phản ánh)
        @Suppress("DEPRECATION")
        val currentUser = intent.getSerializableExtra("USER_INFO") as? User

        // 3. ÁNH XẠ GIAO DIỆN (Đã thêm ô edtTitle)
        val edtTitle = findViewById<EditText>(R.id.edtReportTitle)
        val edtContent = findViewById<EditText>(R.id.edtReportContent)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitReport)

        // 4. XỬ LÝ SỰ KIỆN GỬI VÀ LƯU VÀO DATABASE
        btnSubmit.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val content = edtContent.text.toString().trim()

            // Kiểm tra xem có nhập thiếu không
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Tiêu đề và Nội dung!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentUser != null) {
                // Lấy ngày giờ thực tế hiện tại
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val currentDate = sdf.format(Date())

                // Gọi Database để lưu
                val dbHelper = DatabaseHelper(this)
                val isSuccess = dbHelper.insertReport(currentUser.userId, title, content, currentDate)

                if (isSuccess) {
                    Toast.makeText(this, "Đã gửi phản ánh thành công!", Toast.LENGTH_LONG).show()
                    finish() // Tự động đóng màn hình đỏ, quay về trang Lịch sử
                } else {
                    Toast.makeText(this, "Có lỗi xảy ra, không thể gửi!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lỗi: Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}