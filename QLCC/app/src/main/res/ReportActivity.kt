package com.example.qlcc

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ReportActivity : AppCompatActivity() {

    lateinit var listReports: ListView
    lateinit var radioStatus: RadioGroup
    lateinit var tvFilterTitle: TextView
    lateinit var btnViewReports: Button

    lateinit var adapter: ArrayAdapter<String>

    // ===== DATA GIẢ =====
    val pendingList = arrayListOf(
        "Rò rỉ nước - Căn A101",
        "Hỏng thang máy - Tầng 5"
    )

    val processingList = arrayListOf(
        "Sửa điện hành lang - B202"
    )

    val doneList = arrayListOf(
        "Dọn rác - C303"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // ===== ÁNH XẠ =====
        btnViewReports = findViewById(R.id.btnViewReports)
        listReports = findViewById(R.id.listReports)
        radioStatus = findViewById(R.id.radioStatus)
        tvFilterTitle = findViewById(R.id.tvFilterTitle)

        // ===== ADAPTER =====
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            ArrayList()
        )

        listReports.adapter = adapter

        // ===== CLICK BUTTON =====
        btnViewReports.setOnClickListener {

            tvFilterTitle.visibility = View.VISIBLE
            radioStatus.visibility = View.VISIBLE

            Toast.makeText(this, "Chọn trạng thái để xem", Toast.LENGTH_SHORT).show()
        }

        // ===== CHỌN TRẠNG THÁI =====
        radioStatus.setOnCheckedChangeListener { _, checkedId ->

            listReports.visibility = View.VISIBLE
            adapter.clear()

            when (checkedId) {

                R.id.rbPending -> {
                    adapter.addAll(pendingList)
                }

                R.id.rbProcessing -> {
                    adapter.addAll(processingList)
                }

                R.id.rbDone -> {
                    adapter.addAll(doneList)
                }
            }

            adapter.notifyDataSetChanged()
        }
    }
}
