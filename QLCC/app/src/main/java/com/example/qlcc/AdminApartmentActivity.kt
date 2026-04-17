package com.example.qlcc

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminApartmentActivity : AppCompatActivity() {

    private lateinit var spinnerFloor: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_apartment)

        dbHelper = DatabaseHelper(this)

        spinnerFloor = findViewById(R.id.spinnerFloor)
        recyclerView = findViewById(R.id.recyclerViewApartments)
        fabAdd = findViewById(R.id.fabAddApartment)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Danh sách tầng
        val floors = listOf("Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4", "Tầng 5")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, floors)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFloor.adapter = spinnerAdapter

        // Mặc định load tầng 1
        loadApartmentsForFloor(1)

        // Sự kiện chọn tầng
        spinnerFloor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFloor = position + 1
                loadApartmentsForFloor(selectedFloor)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fabAdd.setOnClickListener {
            Toast.makeText(this, "Chức năng thêm căn hộ mới đang phát triển...", Toast.LENGTH_SHORT).show()
        }
    }

    // === LOAD DỮ LIỆU TỪ DATABASE THEO TẦNG ===
    private fun loadApartmentsForFloor(floor: Int) {
        val apartments = dbHelper.getApartmentsByFloor(floor)

        if (apartments.isEmpty()) {
            Toast.makeText(this, "Tầng $floor chưa có dữ liệu căn hộ", Toast.LENGTH_SHORT).show()
        }

        val adapter = ApartmentAdapter(apartments)
        recyclerView.adapter = adapter
    }
}