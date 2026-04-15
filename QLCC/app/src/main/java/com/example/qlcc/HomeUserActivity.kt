package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HomeUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)

        // 1. Ánh xạ các TextView và Nút bấm
        val tvResidentName = findViewById<TextView>(R.id.tvResidentName)
        val tvRoomNumber = findViewById<TextView>(R.id.tvRoomNumber)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // 2. Nhận thông tin người dùng
        @Suppress("DEPRECATION")
        val currentUser = intent.getSerializableExtra("USER_INFO") as? User

        if (currentUser != null) {
            tvResidentName.text = currentUser.fullName
            tvRoomNumber.text = "Phòng ${currentUser.roomID}"
        }

        // 3. Xử lý sự kiện Đăng xuất
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Đăng xuất")
            builder.setMessage("Bạn có chắc chắn muốn thoát khỏi tài khoản này?")

            builder.setPositiveButton("Thoát ngay") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                // Xóa sạch lịch sử để không thể bấm Back quay lại trang chủ
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Hủy", null)
            builder.show()
        }
    }
}