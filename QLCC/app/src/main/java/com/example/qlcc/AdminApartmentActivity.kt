package com.example.qlcc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminApartmentActivity : AppCompatActivity() {

    private lateinit var rvApartments: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_apartment)

        // 1. Cài đặt Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarAdminApt)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Ánh xạ và khởi tạo
        rvApartments = findViewById(R.id.rvAdminApartments)
        rvApartments.layoutManager = LinearLayoutManager(this)
        dbHelper = DatabaseHelper(this)

        loadApartmentList()
    }

    // Hàm lấy dữ liệu và hiển thị lên RecyclerView
    private fun loadApartmentList() {
        val list = dbHelper.getAllApartments()
        val adapter = ApartmentAdapter(list) { selectedApt ->
            // Khi Admin bấm vào một căn hộ, hiện Dialog chọn trạng thái
            showUpdateStatusDialog(selectedApt)
        }
        rvApartments.adapter = adapter
    }

    private fun showUpdateStatusDialog(apt: Apartment) {
        // Thêm lựa chọn Xóa người ở
        val options = arrayOf("Trống", "Đang ở", "Sửa chữa", "Gán người ở", "Xóa người ở")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quản lý phòng ${apt.roomId}")

        builder.setItems(options) { _, which ->
            when (which) {
                3 -> showSelectUserDialog(apt) // Gán người
                4 -> { // Xóa người
                    if (dbHelper.removeUserFromRoom(apt.roomId)) {
                        Toast.makeText(this, "Đã xóa cư dân khỏi phòng ${apt.roomId}", Toast.LENGTH_SHORT).show()
                        loadApartmentList()
                    }
                }
                else -> {
                    val newStatus = options[which]
                    if (dbHelper.updateApartmentStatus(apt.roomId, newStatus)) {
                        loadApartmentList()
                    }
                }
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun showSelectUserDialog(apt: Apartment) {
        val userList = dbHelper.getAllUsers()
        if (userList.isEmpty()) {
            Toast.makeText(this, "Chưa có cư dân nào!", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy danh sách tên thật từ Database để hiển thị lên Dialog
        val names = userList.map { "${it.fullName} (${it.phoneNumber})" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn cư dân cho phòng ${apt.roomId}")
            .setItems(names) { _, which ->
                val selectedUser = userList[which]
                if (dbHelper.assignUserToRoom(selectedUser.userId, apt.roomId)) {
                    Toast.makeText(this, "Đã gán ${selectedUser.fullName} thành công", Toast.LENGTH_SHORT).show()
                    loadApartmentList() // Load lại để cập nhật tên lên giao diện
                }
            }
            .show()
    }
}