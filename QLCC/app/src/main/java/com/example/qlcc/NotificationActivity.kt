package com.example.qlcc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // 1. Cài đặt Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarNoti)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Cài đặt RecyclerView
        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        // 3. Lấy dữ liệu từ Database và gắn vào Adapter
        val dbHelper = DatabaseHelper(this)
        val notiList = dbHelper.getAllNotifications() // Gọi hàm bạn đã viết ở Bước 3

        rvNotifications.adapter = NotificationAdapter(notiList)
    }
}