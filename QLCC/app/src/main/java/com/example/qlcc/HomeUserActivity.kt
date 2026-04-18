package com.example.qlcc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class HomeUserActivity : AppCompatActivity() {

    // 1. Khai báo biến toàn cục để dùng chung cho các hàm bên dưới
    private var currentUser: User? = null
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)

        // Khởi tạo Database
        dbHelper = DatabaseHelper(this)

        // 2. Ánh xạ các TextView và Nút bấm
        val tvResidentName = findViewById<TextView>(R.id.tvResidentName)
        val tvRoomNumber = findViewById<TextView>(R.id.tvRoomNumber)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // 3. Nhận thông tin người dùng từ Login
        @Suppress("DEPRECATION")
        currentUser = intent.getSerializableExtra("USER_INFO") as? User

        if (currentUser != null) {
            tvResidentName.text = currentUser!!.fullName
            tvRoomNumber.text = "Phòng ${currentUser!!.roomID}"
        }

        // ==========================================
        // 4. CLICK THẺ CẢNH BÁO MÀU CAM -> MỞ TRANG HÓA ĐƠN
        // ==========================================
        val cardAlertInvoice = findViewById<CardView>(R.id.cardAlertInvoice)
        cardAlertInvoice.setOnClickListener {
            val intent = Intent(this, UserInvoiceActivity::class.java)
            if (currentUser != null) {
                intent.putExtra("ROOM_ID", currentUser!!.roomID)
            }
            startActivity(intent)
        }

        // ==========================================
        // 5. XỬ LÝ SỰ KIỆN NÚT ĐĂNG XUẤT
        // ==========================================
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Đăng xuất")
            builder.setMessage("Bạn có chắc chắn muốn thoát khỏi tài khoản này?")

            builder.setPositiveButton("Thoát ngay") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Hủy", null)
            builder.show()
        }

        // ==========================================
        // 6. XỬ LÝ SỰ KIỆN NÚT PHẢN ÁNH
        // ==========================================
        val btnReports = findViewById<CardView>(R.id.btnReports)
        btnReports.setOnClickListener {
            val intent = Intent(this, ReportHistoryActivity::class.java)
            intent.putExtra("USER_INFO", currentUser)
            startActivity(intent)
        }

        // ==========================================
        // 7. XỬ LÝ SỰ KIỆN NÚT "XEM TẤT CẢ" THÔNG BÁO
        // ==========================================
        val tvViewAllNoti = findViewById<TextView>(R.id.tvViewAllNoti)
        tvViewAllNoti.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        // ==========================================
        // 8. XỬ LÝ SỰ KIỆN NÚT HÓA ĐƠN CHÍNH (💰)
        // ==========================================
        val btnInvoices = findViewById<CardView>(R.id.btnInvoices)
        btnInvoices.setOnClickListener {
            val intent = Intent(this, UserInvoiceActivity::class.java)
            if (currentUser != null) {
                intent.putExtra("ROOM_ID", currentUser!!.roomID)
            }
            startActivity(intent)
        }
    }

    // ==========================================
    // 9. HÀM TỰ ĐỘNG CHẠY KHI QUAY LẠI TRANG CHỦ
    // ==========================================
    override fun onResume() {
        super.onResume()
        checkUnpaidInvoices() // Load lại số lượng hóa đơn nợ
    }

    // ==========================================
    // 10. HÀM KIỂM TRA VÀ HIỂN THỊ THẺ CẢNH BÁO NỢ
    // ==========================================
    private fun checkUnpaidInvoices() {
        if (currentUser != null) {
            val cardAlertInvoice = findViewById<CardView>(R.id.cardAlertInvoice)
            val tvAlertMessage = findViewById<TextView>(R.id.tvAlertMessage)

            // Gọi Database đếm số hóa đơn chưa thanh toán của phòng này
            val unpaidCount = dbHelper.getUnpaidInvoiceCount(currentUser!!.roomID)

            if (unpaidCount > 0) {
                // Nếu có nợ -> HIỆN thẻ cam và báo số lượng
                cardAlertInvoice.visibility = View.VISIBLE
                tvAlertMessage.text = "Bạn có $unpaidCount hóa đơn chưa thanh toán. Vui lòng kiểm tra!"
            } else {
                // Nếu đã đóng hết -> ẨN tàng hình thẻ cam luôn
                cardAlertInvoice.visibility = View.GONE
            }
        }
    }
}