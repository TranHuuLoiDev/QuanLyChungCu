package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val tvResidentName = findViewById<TextView>(R.id.tvResidentName)
        val tvRoomNumber = findViewById<TextView>(R.id.tvRoomNumber)

        @Suppress("DEPRECATION")
        val currentUser = intent.getSerializableExtra("USER_INFO") as? User

        if (currentUser != null) {
            tvResidentName.text = currentUser.fullName
            tvRoomNumber.text = "Phòng ${currentUser.roomID}"
        }
    }

    // --- THÊM 2 HÀM NÀY ĐỂ XỬ LÝ MENU ---

    // 1. Gắn file menu_home.xml lên thanh tiêu đề (Action Bar)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    // 2. Lắng nghe sự kiện khi bạn bấm vào một mục trong Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {

            // Hiện bảng thông báo xác nhận giống hệt lúc trước
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Xác nhận đăng xuất")
            builder.setMessage("Bạn có chắc chắn muốn thoát ứng dụng không?")

            builder.setPositiveButton("Đăng xuất") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Hủy", null)
            builder.show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}