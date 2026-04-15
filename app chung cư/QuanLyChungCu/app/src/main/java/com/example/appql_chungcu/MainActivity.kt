package com.example.appql_chungcu

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerFloor: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApartmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apartment_manager)

        spinnerFloor = findViewById(R.id.spinnerFloor)
        recyclerView = findViewById(R.id.recyclerViewApartments)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Danh sách 5 tầng
        val floors = listOf("Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4", "Tầng 5")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, floors)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFloor.adapter = spinnerAdapter

        // Mặc định hiển thị Tầng 1
        loadApartmentsForFloor(1)

        // Khi chọn tầng khác
        spinnerFloor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFloor = position + 1
                loadApartmentsForFloor(selectedFloor)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Hàm load căn hộ theo tầng
    private fun loadApartmentsForFloor(floor: Int) {
        val apartments = mutableListOf<Apartment>()

        for (i in 0..4) {
            val roomNumber = floor * 100 + i
            val name = "Căn hộ $roomNumber"

            val info = when (i % 3) {
                0 -> "2PN • 75m²"
                1 -> "3PN • 95m²"
                else -> "1PN • 55m²"
            }

            val status = if (i % 2 == 0) "Còn trống" else "Đang cho thuê"

            apartments.add(Apartment(roomNumber.toString(), name, info, status))
        }

        adapter = ApartmentAdapter(apartments)
        recyclerView.adapter = adapter
    }
}