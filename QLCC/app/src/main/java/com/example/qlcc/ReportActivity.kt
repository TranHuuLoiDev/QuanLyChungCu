package com.example.qlcc

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // 1. Thiết lập Toolbar màu đỏ
        val toolbar = findViewById<Toolbar>(R.id.toolbarReport)
        setSupportActionBar(toolbar)

        // Hiện nút quay lại và đổi màu nó thành TRẮNG (Cách mới - setTint)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)

        // Dùng setTint thay cho setColorFilter bị gạch ngang
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))

        supportActionBar?.setHomeAsUpIndicator(upArrow)

        // Dùng finish() thay cho onBackPressed() bị gạch ngang
        toolbar.setNavigationOnClickListener {
            finish() }

        // 2. Ánh xạ giao diện
        val edtContent = findViewById<EditText>(R.id.edtReportContent)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitReport)

        // 3. Xử lý sự kiện gửi
        btnSubmit.setOnClickListener {
            val content = edtContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung phản ánh!", Toast.LENGTH_SHORT).show()
                // Tạo rung nhẹ hoặc hiệu ứng viền đỏ ở đây nếu muốn
            } else {
                // --- BƯỚC TIẾP THEO: SẼ VIẾT CODE LƯU VÀO DATABASE Ở ĐÂY ---

                // Tạm thời hiện thông báo đẹp mắt
                Toast.makeText(this, "Cảm ơn bạn! Phản ánh đã được gửi tới BQL.", Toast.LENGTH_LONG).show()
                finish() // Đóng màn hình, quay lại Home
            }
        }
    }
}