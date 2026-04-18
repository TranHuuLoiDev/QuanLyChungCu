package com.example.qlcc
​
 import android.os.Bundle
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
 import androidx.recyclerview.widget.LinearLayoutManager
 import androidx.recyclerview.widget.RecyclerView
 import com.google.android.material.floatingactionbutton.FloatingActionButton
​
 class ResidentActivity : AppCompatActivity() {
​
     private lateinit var recyclerView: RecyclerView
     private lateinit var btnThem: FloatingActionButton
     private lateinit var dbHelper: DatabaseHelper
​
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.resident_list)
​
         dbHelper = DatabaseHelper(this)
         recyclerView = findViewById(R.id.recyclerView)
         btnThem = findViewById(R.id.btnThem)
​
         recyclerView.layoutManager = LinearLayoutManager(this)
         
         // Tạm thời hiển thị thông báo vì chưa có Adapter
         Toast.makeText(this, "Tính năng danh sách cư dân", Toast.LENGTH_SHORT).show()
​
         btnThem.setOnClickListener {
             Toast.makeText(this, "Thêm cư dân mới", Toast.LENGTH_SHORT).show()
         }
     }
 }
