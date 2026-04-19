package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminCreateNotiActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_noti)

        dbHelper = DatabaseHelper(this)

        // 1. Cài đặt Toolbar (Có nút Back)
        val toolbar = findViewById<Toolbar>(R.id.toolbarNoti)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Ánh xạ các View
        val edtTitle = findViewById<EditText>(R.id.edtNotiTitle)
        val edtContent = findViewById<EditText>(R.id.edtNotiContent)
        val rgType = findViewById<RadioGroup>(R.id.rgNotiType)
        val btnSend = findViewById<Button>(R.id.btnSendNoti)

        // 3. Xử lý khi Admin bấm nút Gửi
        btnSend.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val content = edtContent.text.toString().trim()

            // Lấy loại thông báo dựa vào RadioButton nào đang được check
            val type = if (rgType.checkedRadioButtonId == R.id.rbUrgent) "Khẩn cấp" else "Thông thường"

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Tiêu đề và Nội dung!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tự động lấy ngày giờ hiện tại theo format y hệt trong Database (VD: 2026-04-19 20:47)
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

            // Tạm thời gán admin_id = 1. (Sau này nếu có nhiều admin thì bạn truyền từ Login sang)
            val adminId = 1

            // Đẩy vào Database
            val isSuccess = dbHelper.insertNotification(adminId, title, content, type, currentDate)

            if (isSuccess) {
                Toast.makeText(this, "Đã phát thông báo thành công!", Toast.LENGTH_LONG).show()

                // ==========================================
                // ĐÃ SỬA Ở ĐÂY: CHUYỂN THẲNG VỀ TRANG DANH SÁCH VỚI QUYỀN ADMIN
                // ==========================================
                val intent = Intent(this, NotificationActivity::class.java)
                intent.putExtra("ROLE", "ADMIN")

                // Thêm 2 cờ này để xóa lịch sử trang cũ, tránh bị lỗi khi bấm nút Back của điện thoại
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                finish() // Đóng trang soạn thảo hiện tại
            } else {
                Toast.makeText(this, "Lỗi khi lưu thông báo!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}