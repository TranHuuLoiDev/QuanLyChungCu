package com.example.qlcc

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class AdminBillEditorActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinnerApartment: Spinner
    private lateinit var btnCheckInDate: Button
    private lateinit var btnCheckOutDate: Button
    private lateinit var edtElectric: EditText
    private lateinit var edtWater: EditText
    private lateinit var edtRent: EditText // Giao diện bạn để ID là edtRent (Tiền gửi xe)
    private lateinit var edtMonths: EditText
    private lateinit var tvTotalRent: TextView
    private lateinit var btnSave: Button

    private var totalAmount: Double = 0.0
    private var roomList = listOf<Apartment>()

    private var editingInvoice: Invoice? = null // Biến để biết đang sửa hay thêm mới

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bill_editor)

        dbHelper = DatabaseHelper(this)

        // 1. Ánh xạ View
        spinnerApartment = findViewById(R.id.spinnerApartment)
        btnCheckInDate = findViewById(R.id.btnCheckInDate)
        btnCheckOutDate = findViewById(R.id.btnCheckOutDate)
        edtElectric = findViewById(R.id.edtElectric)
        edtWater = findViewById(R.id.edtWater)
        edtRent = findViewById(R.id.edtRent)
        edtMonths = findViewById(R.id.edtMonths)
        tvTotalRent = findViewById(R.id.tvTotalRent)
        btnSave = findViewById(R.id.btnSave)

        // 2. Khởi chạy các hàm logic
        setupSpinner()
        setupDatePickers()
        setupAutoCalculate()
        setupSaveButton()

        // Kiểm tra xem có dữ liệu sửa gửi từ trang danh sách sang không
        editingInvoice = intent.getSerializableExtra("EDIT_INVOICE") as? Invoice

        if (editingInvoice != null) {
            // NẾU LÀ CHẾ ĐỘ SỬA: Đổ dữ liệu cũ vào các ô
            fillDataForEdit(editingInvoice!!)
        }
    }
    // 1. ĐỔ DỮ LIỆU PHÒNG VÀO SPINNER
    private fun setupSpinner() {
        roomList = dbHelper.getAllApartments()
        // Chỉ lấy danh sách tên phòng (VD: A-101, B-202) để đưa vào Spinner
        val roomNames = roomList.map { it.roomId }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roomNames)
        spinnerApartment.adapter = adapter
    }

    // 2. CÀI ĐẶT BỘ CHỌN LỊCH
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        val dateSetListener = { button: Button ->
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                button.text = selectedDate
            }
        }

        btnCheckInDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener(btnCheckInDate),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnCheckOutDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener(btnCheckOutDate),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    // ==========================================
    // 3. AUTO-CALCULATE TÍNH TIỀN TỰ ĐỘNG
    // ==========================================
    private fun setupAutoCalculate() {
        // Tạo một TextWatcher (Bộ theo dõi) để xem khi nào Admin gõ phím
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                calculateTotal() // Gọi hàm tính toán ngay khi chữ bị thay đổi
            }
        }

        // Gắn bộ theo dõi vào cả 4 ô nhập liệu
        edtElectric.addTextChangedListener(textWatcher)
        edtWater.addTextChangedListener(textWatcher)
        edtRent.addTextChangedListener(textWatcher)
        edtMonths.addTextChangedListener(textWatcher)
    }

    private fun calculateTotal() {
        // 1. Lấy dữ liệu từ các ô nhập (nếu bỏ trống thì tính là 0)
        val electric = edtElectric.text.toString().toDoubleOrNull() ?: 0.0
        val water = edtWater.text.toString().toDoubleOrNull() ?: 0.0
        val parking = edtRent.text.toString().toDoubleOrNull() ?: 0.0 // Đây là tiền gửi xe

        // Mặc định nếu chưa nhập số tháng thì tính là 1 tháng
        val months = edtMonths.text.toString().toDoubleOrNull() ?: 1.0

        // 2. Khai báo Tiền thuê phòng cố định (5.000.000 VNĐ)
        val baseRentPerMonth = 5000000.0

        // 3. CÔNG THỨC CHUẨN:
        // Tổng tiền = (Tiền phòng 1 tháng * Số tháng) + Điện + Nước + Tiền xe
        totalAmount = (baseRentPerMonth * months) + electric + water + parking

        // 4. Định dạng và hiển thị lên giao diện
        val formatter = DecimalFormat("#,###")
        tvTotalRent.text = "Tổng thanh toán: ${formatter.format(totalAmount)} VNĐ"
    }

    private fun fillDataForEdit(invoice: Invoice) {
        // 1. Đổi tiêu đề nút và text
        btnSave.text = "Cập nhật thay đổi"

        // 2. Điền số tiền (Tạm thời chia ngược lại hoặc điền trực tiếp tùy logic của bạn)
        // Ở đây mình ví dụ điền thẳng vào các ô:
        edtElectric.setText("0") // Vì Database không lưu tách lẻ nên bạn có thể nhập lại
        edtMonths.setText("1")

        // 3. Hiển thị tổng tiền cũ
        val formatter = java.text.DecimalFormat("#,###")
        tvTotalRent.text = "Tổng thanh toán: ${formatter.format(invoice.amount)} VNĐ"
        totalAmount = invoice.amount
    }

    // 4. LƯU DỮ LIỆU VÀO DATABASE
    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            if (roomList.isEmpty()) {
                Toast.makeText(this, "Không có căn hộ nào trong hệ thống!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRoom = spinnerApartment.selectedItem.toString()
            val checkIn = btnCheckInDate.text.toString()
            val checkOut = btnCheckOutDate.text.toString()

            // Kiểm tra xem đã chọn ngày chưa
            if (checkIn == "Chọn ngày nhận phòng" || checkOut == "Chọn ngày trả phòng") {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo tiêu đề và ngày tháng
            val invoiceTitle = "Hóa đơn ($checkIn - $checkOut)"
            val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())

            val isSuccess: Boolean

            if (editingInvoice != null) {
                // --- CHẾ ĐỘ CẬP NHẬT (SỬA HÓA ĐƠN CŨ) ---
                val updatedInvoice = Invoice(
                    invoiceId = editingInvoice!!.invoiceId, // Lấy ID của hóa đơn cũ
                    roomId = selectedRoom,
                    invoiceTitle = invoiceTitle,
                    amount = totalAmount,
                    createdDate = currentDate,
                    status = editingInvoice!!.status // Giữ nguyên trạng thái thanh toán cũ
                )
                isSuccess = dbHelper.updateFullInvoice(updatedInvoice)
            } else {
                // --- CHẾ ĐỘ THÊM MỚI ---
                isSuccess = dbHelper.insertInvoice(selectedRoom, invoiceTitle, totalAmount, currentDate)
            }

            // Kiểm tra kết quả và đóng màn hình
            if (isSuccess) {
                Toast.makeText(this, "Đã lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show()
                finish() // Đóng màn hình này và quay lại danh sách
            } else {
                Toast.makeText(this, "Lỗi khi lưu hóa đơn!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}