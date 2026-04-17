package com.example.qlcc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminDashboardActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // 1. Kích hoạt thanh Toolbar Admin
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarAdmin)
        setSupportActionBar(toolbar)

        // 2. Hiển thị thông tin Admin (Nếu bạn muốn hiện tên Admin lên màn hình)
        val tvAdminName = findViewById<TextView>(R.id.tvAdminName)
        @Suppress("DEPRECATION")
        val currentAdmin = intent.getSerializableExtra("ADMIN_INFO") as? Admin

        if (currentAdmin != null) {
            tvAdminName.text = "Chào, ${currentAdmin.adminFullname ?: currentAdmin.adminUsername}"
        }

        // 3. Ánh xạ nút Đăng xuất màu đỏ
        val btnLogoutAdmin = findViewById<Button>(R.id.btnLogoutAdmin)

        // 4. Xử lý sự kiện Đăng xuất
        btnLogoutAdmin.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Xác nhận đăng xuất")
            builder.setMessage("Bạn có chắc chắn muốn thoát khỏi quyền quản trị?")

            builder.setPositiveButton("Đăng xuất") { _, _ ->
                // Chuyển về màn hình Login
                val intent = Intent(this, LoginActivity::class.java)

                // FLAG quan trọng: Xóa sạch các màn hình cũ để không quay lại được bằng nút Back
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish() // Đóng màn hình Admin
            }

            builder.setNegativeButton("Hủy", null)
            builder.show()
        }

        // 5. Click vào "Trạng thái Căn hộ" → chuyển sang AdminApartmentActivity
        val btnManageRooms = findViewById<CardView>(R.id.btnManageRooms)

        btnManageRooms.setOnClickListener {
            val intent = Intent(this, AdminApartmentActivity::class.java)
            startActivity(intent)
        }
        // --- CÁC NÚT CHỨC NĂNG KHÁC (Sẽ code tiếp ở các bài sau) ---
        val btnManageUsers = findViewById<androidx.cardview.widget.CardView>(R.id.btnManageUsers)
        btnManageUsers.setOnClickListener {
            Toast.makeText(this, "Chức năng Quản lý cư dân đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }
}