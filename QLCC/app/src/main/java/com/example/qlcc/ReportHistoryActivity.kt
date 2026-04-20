package com.example.qlcc

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ReportHistoryActivity : AppCompatActivity() {

    private lateinit var rvReports: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        dbHelper = DatabaseHelper(this)
        rvReports = findViewById(R.id.rvReports)
        rvReports.layoutManager = LinearLayoutManager(this)

        @Suppress("DEPRECATION")
        currentUser = intent.getSerializableExtra("USER_INFO") as? User

        val btnCreateNewReport = findViewById<Button>(R.id.btnCreateNewReport)
        btnCreateNewReport.setOnClickListener {
            showAddReportDialog()
        }

        loadReports()
    }

    private fun showAddReportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_report, null)
        val edtTitle = dialogView.findViewById<EditText>(R.id.edtReportTitle)
        val edtContent = dialogView.findViewById<EditText>(R.id.edtReportContent)

        AlertDialog.Builder(this)
            .setTitle("Gửi phản ánh mới")
            .setView(dialogView)
            .setPositiveButton("Gửi") { _, _ ->
                val title = edtTitle.text.toString().trim()
                val content = edtContent.text.toString().trim()

                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val userId = currentUser?.userId ?: 1

                    // 👉 THÊM NGÀY GIỜ (fix lỗi thiếu tham số)
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val currentDate = sdf.format(Date())

                    // 👉 GỌI HÀM MỚI (Boolean)
                    val success = dbHelper.insertReport(userId, title, content, currentDate)

                    if (success) {
                        Toast.makeText(this, "Gửi thành công!", Toast.LENGTH_SHORT).show()
                        loadReports()
                    } else {
                        Toast.makeText(this, "Gửi thất bại!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun loadReports() {
        val userId = currentUser?.userId ?: 1
        val list = dbHelper.getReportsByUserId(userId)

        // 👉 Đảm bảo đúng kiểu MutableList cho Adapter
        rvReports.adapter = ReportAdapter(list.toMutableList())
    }
}
