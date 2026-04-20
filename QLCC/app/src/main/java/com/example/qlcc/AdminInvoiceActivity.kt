package com.example.qlcc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminInvoiceActivity : AppCompatActivity() {

    private lateinit var rvInvoices: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_invoice)

        // 1. Cài đặt Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarAdminInvoice)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_revert)
        upArrow?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Khởi tạo
        rvInvoices = findViewById(R.id.rvAdminInvoices)
        rvInvoices.layoutManager = LinearLayoutManager(this)
        dbHelper = DatabaseHelper(this)

        loadInvoiceList()

        // Bắt sự kiện khi ấn vào nút Dấu Cộng tròn ở góc
        val fabAddInvoice = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddInvoice)
        fabAddInvoice.setOnClickListener {
            val intent = Intent(this, AdminBillEditorActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        // Mỗi khi quay lại màn hình này, tự động tải lại danh sách mới nhất
        loadInvoiceList()
    }

    private fun loadInvoiceList() {
        val list = dbHelper.getAllInvoices()
        val adapter = InvoiceAdapter(list) { selectedInvoice ->
            val options = arrayOf("Cập nhật trạng thái", "Chỉnh sửa chi tiết", "Xóa hóa đơn")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Tùy chọn cho HĐ #${selectedInvoice.invoiceId}")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> showUpdateStatusDialog(selectedInvoice)
                    1 -> {
                        val intent = Intent(this, AdminBillEditorActivity::class.java)
                        intent.putExtra("EDIT_INVOICE", selectedInvoice)
                        startActivity(intent)
                    }
                    2 -> {
                        //code xử lý Xóa
                        confirmDelete(selectedInvoice)
                    }
                }
            }
            builder.show()
        }
        rvInvoices.adapter = adapter
    }

    // Hàm hiện thông báo xác nhận trước khi xóa
    private fun confirmDelete(invoice: Invoice) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa hóa đơn của phòng ${invoice.roomId} không?")
            .setPositiveButton("Xóa") { _, _ ->
                val isDeleted = dbHelper.deleteInvoice(invoice.invoiceId)
                if (isDeleted) {
                    Toast.makeText(this, "Đã xóa hóa đơn", Toast.LENGTH_SHORT).show()
                    loadInvoiceList() // gọi lại hàm này để cập nhật lại danh sách trên màn hình
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Hàm cập nhật trạng thái (Đã sửa để đảm bảo cập nhật giao diện ngay lập tức)
    private fun showUpdateStatusDialog(invoice: Invoice) {
        val options = arrayOf("Đã thanh toán", "Chưa thanh toán")
        AlertDialog.Builder(this)
            .setTitle("Đổi trạng thái thanh toán")
            .setItems(options) { _, which ->
                val newStatus = options[which]
                val isSuccess = dbHelper.updateInvoiceStatus(invoice.invoiceId, newStatus)
                if (isSuccess) {
                    Toast.makeText(this, "Đã đổi thành: $newStatus", Toast.LENGTH_SHORT).show()
                    loadInvoiceList() // Quan trọng: Phải gọi lại hàm này để màu sắc xanh/đỏ thay đổi ngay
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}