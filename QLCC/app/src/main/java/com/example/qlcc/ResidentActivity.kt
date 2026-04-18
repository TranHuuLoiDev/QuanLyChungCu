package com.example.qlcc
 import android.os.Bundle
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
 import androidx.recyclerview.widget.LinearLayoutManager
 import androidx.recyclerview.widget.RecyclerView
 import com.google.android.material.floatingactionbutton.FloatingActionButton
 class ResidentActivity : AppCompatActivity() {

     private lateinit var recyclerView: RecyclerView
     private lateinit var btnThem: FloatingActionButton
     private lateinit var dbHelper: DatabaseHelper
     private lateinit var adapter: ResidentAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.resident_list)

         dbHelper = DatabaseHelper(this)
         recyclerView = findViewById(R.id.recyclerView)
         btnThem = findViewById(R.id.btnThem)

         recyclerView.layoutManager = LinearLayoutManager(this)
         
         loadResidentList()

         btnThem.setOnClickListener {
             Toast.makeText(this, "Tính năng Thêm cư dân sẽ được cập nhật", Toast.LENGTH_SHORT).show()
         }
     }

     private fun loadResidentList() {
         val list = dbHelper.getAllUsers()
         adapter = ResidentAdapter(list, 
             onEditClick = { user ->
                 Toast.makeText(this, "Sửa: ${user.fullName}", Toast.LENGTH_SHORT).show()
             },
             onDeleteClick = { user ->
                 Toast.makeText(this, "Xóa: ${user.fullName}", Toast.LENGTH_SHORT).show()
             }
         )
         recyclerView.adapter = adapter
     }
 }
