package com.example.qlcc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminAnnouncementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_announcement)

        val etTitle = findViewById<EditText>(R.id.et_title)
        val etContent = findViewById<EditText>(R.id.et_content)
        val btnPost = findViewById<Button>(R.id.btn_post_announcement)

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show()
            } else {
                // Logic gửi thông báo sẽ được thực hiện ở đây (Lưu vào DB)
                Toast.makeText(this, "Đã đăng thông báo thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
