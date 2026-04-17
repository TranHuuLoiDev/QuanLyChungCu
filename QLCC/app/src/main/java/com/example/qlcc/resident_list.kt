package com.example.qlcc

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var cuDanList: ArrayList<CuDan>
    private lateinit var filteredList: ArrayList<CuDan>
    private lateinit var adapter: CuDanAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnThem: FloatingActionButton
    private lateinit var edtSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resident_list)

        recyclerView = findViewById(R.id.recyclerView)
        btnThem = findViewById(R.id.btnThem)

        cuDanList = ArrayList()
        filteredList = ArrayList()

        adapter = CuDanAdapter(this, filteredList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnThem.setOnClickListener {
            showDialogThem()
        }

        addSampleData()
    }

    private fun showDialogThem() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm cư dân mới")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etHoTen = EditText(this)
        etHoTen.hint = "Họ và tên"
        layout.addView(etHoTen)

        val etSdt = EditText(this)
        etSdt.hint = "Số điện thoại"
        layout.addView(etSdt)

        val etPhong = EditText(this)
        etPhong.hint = "Số phòng"
        layout.addView(etPhong)

        builder.setView(layout)

        builder.setPositiveButton("Lưu") { _, _ ->
            val hoTen = etHoTen.text.toString()
            val sdt = etSdt.text.toString()
            val phong = etPhong.text.toString()

            if (hoTen.isNotEmpty() && sdt.isNotEmpty() && phong.isNotEmpty()) {
                val cuDanMoi = CuDan(hoTen, sdt, phong)
                cuDanList.add(cuDanMoi)
                filteredList.add(cuDanMoi)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Đã thêm!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun addSampleData() {
        cuDanList.add(CuDan("Nguyễn Văn A", "0912345678", "101"))
        cuDanList.add(CuDan("Trần Thị B", "0987654321", "205"))
        filteredList.addAll(cuDanList)
        adapter.notifyDataSetChanged()
    }
}
