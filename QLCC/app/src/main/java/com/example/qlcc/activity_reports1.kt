package com.example.qlcc


import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.qlcc.R

class AdminReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CHỖ NÀY QUAN TRỌNG: Phải khớp với tên file XML của bạn là activity_reports.xml
        setContentView(R.layout.activity_reports)

        // 1. Kích hoạt Toolbar (ID là toolbar trong activity_reports.xml)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Hiện nút mũi tên quay lại trên Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Khi bấm mũi tên thì quay về Dashboard
        }

        // 2. Ánh xạ các view từ XML activity_reports.xml
        val btnView = findViewById<Button>(R.id.btnViewReports)
        val tvFilter = findViewById<TextView>(R.id.tvFilterTitle)
        val radioGroup = findViewById<RadioGroup>(R.id.radioStatus)
        val listView = findViewById<ListView>(R.id.listReports)

        // 3. Xử lý logic khi bấm nút "Xem thông tin phản ánh"
        btnView.setOnClickListener {
            // Chuyển trạng thái từ ẨN (gone) sang HIỆN (visible)
            tvFilter.visibility = View.VISIBLE
            radioGroup.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE

            Toast.makeText(this, "Đang tải danh sách phản ánh...", Toast.LENGTH_SHORT).show()

            // Note: Sau này bro sẽ viết code lấy dữ liệu thật từ database đổ vào listView ở đây
        }
    }
}
