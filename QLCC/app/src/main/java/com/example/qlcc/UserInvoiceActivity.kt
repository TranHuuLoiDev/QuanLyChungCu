package com.example.qlcc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserInvoiceActivity : AppCompatActivity() {

    private lateinit var rvInvoices: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_invoice)

        // 1. Cài đặt Toolbar (Có nút Back)
        val toolbar = findViewById<Toolbar>(R.id.toolbarUserInvoice)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Khởi tạo
        rvInvoices = findViewById(R.id.rvUserInvoices)
        rvInvoices.layoutManager = LinearLayoutManager(this)
        dbHelper = DatabaseHelper(this)

        loadUserInvoices()
    }

    private fun loadUserInvoices() {
        // 1. Nhận mã phòng từ HomeUserActivity gửi sang (nếu không có thì để rỗng "")
        val myRoomId = intent.getStringExtra("ROOM_ID") ?: ""

        // 2. Lấy danh sách hóa đơn đúng của phòng đó
        val list = dbHelper.getInvoicesByRoom(myRoomId)

        val adapter = InvoiceAdapter(list) { selectedInvoice ->
            // Khi User bấm vào hóa đơn
            if (selectedInvoice.status == "Chưa thanh toán") {
                Toast.makeText(this, "Vui lòng đến Quầy quản lý hoặc chuyển khoản để thanh toán Hóa đơn này!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Hóa đơn này đã được thanh toán. Cảm ơn bạn!", Toast.LENGTH_SHORT).show()
            }
        }
        rvInvoices.adapter = adapter
    }
}