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
        val statuses = arrayOf("Trống", "Đang ở", "Sửa chữa")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cập nhật trạng thái phòng ${apt.roomId}")

        builder.setItems(statuses) { _, which ->
            val newStatus = statuses[which]

            // Gọi hàm cập nhật vào Database
            val isSuccess = dbHelper.updateApartmentStatus(apt.roomId, newStatus)

            if (isSuccess) {
                Toast.makeText(this, "Đã đổi trạng thái phòng ${apt.roomId} thành $newStatus", Toast.LENGTH_SHORT).show()
                loadApartmentList() // Tải lại danh sách để cập nhật giao diện
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy", null)
        builder.show()
    }
}