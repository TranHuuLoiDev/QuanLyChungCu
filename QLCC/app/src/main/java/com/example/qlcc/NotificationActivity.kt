package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotificationActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvNotifications: RecyclerView
    private var userRole: String = "USER" // Biến lưu thân phận, mặc định là USER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // 1. Nhận thẻ Thân phận từ màn hình trước
        userRole = intent.getStringExtra("ROLE") ?: "USER"

        // 2. Cài đặt Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarNoti)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 3. Khởi tạo Database và RecyclerView
        dbHelper = DatabaseHelper(this)
        rvNotifications = findViewById(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        // 4. Xử lý nút Dấu Cộng (+) ẩn/hiện theo quyền
        val btnAddNoti = findViewById<FloatingActionButton>(R.id.btnAddNoti)
        if (userRole == "ADMIN") {
            btnAddNoti.visibility = View.VISIBLE
            btnAddNoti.setOnClickListener {
                val intent = Intent(this, AdminCreateNotiActivity::class.java)
                startActivity(intent)
            }
        } else {
            btnAddNoti.visibility = View.GONE // Cư dân thì tàng hình
        }
    }

    // Tự động làm mới danh sách mỗi khi mở lại màn hình này
    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    // ==========================================
    // Hàm tải danh sách và gắn Adapter
    // ==========================================
    private fun loadNotifications() {
        val notiList = dbHelper.getAllNotifications()

        val adapter = NotificationAdapter(notiList,
            onItemClick = { noti ->
                // Khi Menu chọn "Chỉnh sửa" -> Nó sẽ chạy vào đây
                if (userRole == "ADMIN") {
                    showEditNotiDialog(noti)
                } else {
                    // Cư dân click bình thường thì chỉ hiện thông báo nhỏ
                    Toast.makeText(this, "Thông báo: ${noti.title}", Toast.LENGTH_SHORT).show()
                }
            },
            onItemLongClick = { noti ->
                // Khi Menu chọn "Xóa thông báo" -> Nó sẽ chạy vào đây
                if (userRole == "ADMIN") {
                    showDeleteConfirmDialog(noti)
                }
            }
        )
        rvNotifications.adapter = adapter
    }

    // ==========================================
    // Hàm hiển thị hộp thoại CHỈNH SỬA
    // ==========================================
    private fun showEditNotiDialog(noti: NotificationModel) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chỉnh sửa thông báo")

        // Tạo giao diện nhập liệu trực tiếp bằng code
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)

        val edtTitle = EditText(this)
        edtTitle.setText(noti.title)
        edtTitle.hint = "Tiêu đề thông báo"
        layout.addView(edtTitle)

        val edtContent = EditText(this)
        edtContent.setText(noti.content)
        edtContent.hint = "Nội dung chi tiết"
        layout.addView(edtContent)

        builder.setView(layout)

        builder.setPositiveButton("Cập nhật") { _, _ ->
            val newTitle = edtTitle.text.toString().trim()
            val newContent = edtContent.text.toString().trim()

            if (newTitle.isNotEmpty() && newContent.isNotEmpty()) {
                // Gọi lệnh update dưới DB
                val success = dbHelper.updateNotification(noti.notifyId, newTitle, newContent, noti.type)
                if (success) {
                    Toast.makeText(this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    loadNotifications() // Load lại danh sách mới
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    // ==========================================
    // Hàm hiển thị hộp thoại XÓA
    // ==========================================
    private fun showDeleteConfirmDialog(noti: NotificationModel) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa thông báo:\n'${noti.title}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                val success = dbHelper.deleteNotification(noti.notifyId)
                if (success) {
                    Toast.makeText(this, "Đã xóa thông báo!", Toast.LENGTH_SHORT).show()
                    loadNotifications()
                } else {
                    Toast.makeText(this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}