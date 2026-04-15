package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnLogin: Button

    // Khai báo DatabaseHelper
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Ánh xạ giao diện
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Khởi tạo Database
        dbHelper = DatabaseHelper(this)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Bước 1: Thử tìm trong phòng Admin trước
            val loggedInAdmin = dbHelper.checkAdminLogin(username, password)

            if (loggedInAdmin != null) {
                // Nếu tìm thấy -> Đích thị là Ban Quản Lý
                Toast.makeText(this, "Xin chào Ban Quản Lý!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AdminDashboardActivity::class.java)
                intent.putExtra("ADMIN_INFO", loggedInAdmin) // Chú ý: Nhãn dán gói hàng là ADMIN_INFO
                startActivity(intent)
                finish()

            } else {
                // Bước 2: Không thấy bên Admin, chạy qua phòng Cư dân tìm
                val loggedInUser = dbHelper.checkLogin(username, password)

                if (loggedInUser != null) {
                    // Nếu tìm thấy -> Là Cư dân bình thường
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeUserActivity::class.java)
                    intent.putExtra("USER_INFO", loggedInUser) // Nhãn dán gói hàng là USER_INFO
                    startActivity(intent)
                    finish()

                } else {
                    // Bước 3: Tìm cả 2 phòng đều không có mặt
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}