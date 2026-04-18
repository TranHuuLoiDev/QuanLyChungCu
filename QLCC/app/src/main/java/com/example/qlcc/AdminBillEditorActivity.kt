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

    // 2. CÀI ĐẶT BỘ CHỌN LỊCH VÀ TỰ ĐỘNG TÍNH THÁNG
    // ==========================================
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        val dateSetListener = { button: Button ->
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                button.text = selectedDate

                // Cứ mỗi lần chọn xong ngày là gọi hàm tự tính số tháng
                autoFillMonths()
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

    // Hàm phụ: Tự động trừ ngày để tính ra số tháng thuê
    private fun autoFillMonths() {
        val checkIn = btnCheckInDate.text.toString()
        val checkOut = btnCheckOutDate.text.toString()

        if (checkIn != "Chọn ngày nhận phòng" && checkOut != "Chọn ngày trả phòng") {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            try {
                val dIn = sdf.parse(checkIn)
                val dOut = sdf.parse(checkOut)

                if (dOut.after(dIn)) {
                    val calIn = Calendar.getInstance().apply { time = dIn }
                    val calOut = Calendar.getInstance().apply { time = dOut }

                    // Tính chênh lệch tháng
                    var diffMonths = (calOut.get(Calendar.YEAR) - calIn.get(Calendar.YEAR)) * 12 +
                            (calOut.get(Calendar.MONTH) - calIn.get(Calendar.MONTH))

                    // Nếu ngày trả nhỏ hơn ngày nhận (VD: 15/4 đến 14/5) -> Chưa đủ 1 tháng tròn
                    if (calOut.get(Calendar.DAY_OF_MONTH) < calIn.get(Calendar.DAY_OF_MONTH)) {
                        diffMonths--
                    }

                    // Tự động điền vào ô Số tháng (nếu >= 1)
                    if (diffMonths >= 1) {
                        edtMonths.setText(diffMonths.toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    // 4. LƯU HOẶC CẬP NHẬT DATABASE (CÓ CHECK LOGIC NGÀY THÁNG)
    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            if (roomList.isEmpty()) {
                Toast.makeText(this, "Không có căn hộ nào trong hệ thống!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRoom = spinnerApartment.selectedItem.toString()
            val checkIn = btnCheckInDate.text.toString()
            val checkOut = btnCheckOutDate.text.toString()

            // 1. Kiểm tra đã chọn đủ 2 ngày chưa
            if (checkIn == "Chọn ngày nhận phòng" || checkOut == "Chọn ngày trả phòng") {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày nhận và trả phòng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. RÀNG BUỘC LOGIC NGÀY THÁNG
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            try {
                val dIn = sdf.parse(checkIn)
                val dOut = sdf.parse(checkOut)

                // 2.1: Ngày trả phải sau ngày nhận
                if (!dOut.after(dIn)) {
                    Toast.makeText(this, "Ngày trả phòng phải LỚN HƠN ngày nhận phòng!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val calIn = Calendar.getInstance().apply { time = dIn }
                val calOut = Calendar.getInstance().apply { time = dOut }

                var realDiffMonths = (calOut.get(Calendar.YEAR) - calIn.get(Calendar.YEAR)) * 12 +
                        (calOut.get(Calendar.MONTH) - calIn.get(Calendar.MONTH))

                if (calOut.get(Calendar.DAY_OF_MONTH) < calIn.get(Calendar.DAY_OF_MONTH)) {
                    realDiffMonths--
                }

                // 2.2: Thời gian thuê phải tối thiểu 1 tháng tròn
                if (realDiffMonths < 1) {
                    Toast.makeText(this, "Thời gian thuê chưa đủ 1 tháng tròn!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // 2.3: Số tháng tự nhập vào (edtMonths) phải KHỚP với thực tế
                val inputMonths = edtMonths.text.toString().toIntOrNull() ?: 0
                if (inputMonths != realDiffMonths) {
                    Toast.makeText(this, "Số tháng thuê ($inputMonths) không khớp với thời gian trên lịch ($realDiffMonths tháng)!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Lỗi đọc dữ liệu ngày tháng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Tiến hành Lưu (Nếu vượt qua hết các kiểm tra trên)
            val invoiceTitle = "Hóa đơn ($checkIn - $checkOut)"
            val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())

            val isSuccess: Boolean
            if (editingInvoice != null) {
                val updatedInvoice = Invoice(
                    invoiceId = editingInvoice!!.invoiceId,
                    roomId = selectedRoom,
                    invoiceTitle = invoiceTitle,
                    amount = totalAmount,
                    createdDate = currentDate,
                    status = editingInvoice!!.status
                )
                isSuccess = dbHelper.updateFullInvoice(updatedInvoice)
            } else {
                isSuccess = dbHelper.insertInvoice(selectedRoom, invoiceTitle, totalAmount, currentDate)
            }

            if (isSuccess) {
                Toast.makeText(this, "Đã lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi lưu hóa đơn!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}