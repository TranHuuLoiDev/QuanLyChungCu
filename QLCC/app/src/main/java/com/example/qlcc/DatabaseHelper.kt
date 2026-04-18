package com.example.qlcc

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, "QuanLyCC.db", null, 1) {

    private val dbPath: String = context.getDatabasePath("QuanLyCC.db").path

    init {
        // Kiểm tra xem file đã tồn tại trong bộ nhớ máy chưa
        if (!File(dbPath).exists()) {
            copyDatabase()
        }
    }

    private fun copyDatabase() {
        try {
            val inputStream = context.assets.open("QuanLyCC.db")
            val outputFile = File(dbPath)

            if (outputFile.parentFile?.exists() == false) {
                outputFile.parentFile?.mkdirs()
            }

            val outputStream = FileOutputStream(dbPath)
            inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {}

    // ==========================================
    // 1. Hàm kiểm tra Đăng nhập cho CƯ DÂN
    // ==========================================
    fun checkLogin(username: String, pass: String): User? {
        val db = this.readableDatabase
        var user: User? = null

        val cursor = db.rawQuery("SELECT * FROM Users WHERE user_name = ? AND user_password = ?", arrayOf(username, pass))

        if (cursor.moveToFirst()) {
            user = User(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow("user_fullname")),
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("user_phonenumber")),
                roomID = cursor.getString(cursor.getColumnIndexOrThrow("user_roomID"))
            )
        }
        cursor.close()
        return user
    }

    // ==========================================
    // 2. Hàm kiểm tra Đăng nhập cho ADMIN (Đứng ngang hàng)
    // ==========================================
    fun checkAdminLogin(username: String, pass: String): Admin? {
        val db = this.readableDatabase
        var admin: Admin? = null

        val cursor = db.rawQuery("SELECT * FROM Admin WHERE admin_username = ? AND admin_password = ?", arrayOf(username, pass))

        if (cursor.moveToFirst()) {
            admin = Admin(
                adminId = cursor.getInt(cursor.getColumnIndexOrThrow("admin_id")),
                adminUsername = cursor.getString(cursor.getColumnIndexOrThrow("admin_username")),
                adminFullname = cursor.getString(cursor.getColumnIndexOrThrow("admin_fullname")),
                adminPhonenumber = cursor.getString(cursor.getColumnIndexOrThrow("admin_phonenumber")),
                adminRole = cursor.getString(cursor.getColumnIndexOrThrow("admin_role"))
            )
        }
        cursor.close()
        return admin
    }

    // ==========================================
    // 3. Hàm lấy danh sách phản ánh của một Cư dân
    // ==========================================
    fun getReportsByUserId(userId: Int): List<Report> {
        val reportList = mutableListOf<Report>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Reports WHERE user_id = ? ORDER BY report_id DESC", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val report = Report(
                    reportId = cursor.getInt(cursor.getColumnIndexOrThrow("report_id")),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    createdDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
                reportList.add(report)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return reportList
    }

    // ==========================================
    // 4. HÀM MỚI: Thêm phản ánh vào Database
    // ==========================================
    fun insertReport(userId: Int, title: String, content: String, date: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()

        values.put("user_id", userId)
        values.put("title", title)
        values.put("content", content)
        values.put("created_date", date)
        values.put("status", "Chờ xử lý")

        // insert trả về -1 nếu bị lỗi
        val result = db.insert("Reports", null, values)
        db.close()
        return result != -1L
    }

    // Lấy tất cả thông báo
    fun getAllNotifications(): List<NotificationModel> {
        val list = mutableListOf<NotificationModel>()
        val db = this.readableDatabase
        // Giả sử bảng của bạn tên là Notifications (nếu tên khác bạn tự đổi lại nhé)
        val cursor = db.rawQuery("SELECT * FROM Notifications ORDER BY notify_id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val noti = NotificationModel(
                    notifyId = cursor.getInt(cursor.getColumnIndexOrThrow("notify_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                )
                list.add(noti)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // ==========================================
    // Hàm lấy danh sách tất cả căn hộ cho ADMIN
    // ==========================================
    fun getAllApartments(): List<Apartment> {
        val list = mutableListOf<Apartment>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Apartments ORDER BY room_id ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val apt = Apartment(
                    roomId = cursor.getString(cursor.getColumnIndexOrThrow("room_id")),
                    roomStatus = cursor.getString(cursor.getColumnIndexOrThrow("room_status")),
                    roomArea = cursor.getDouble(cursor.getColumnIndexOrThrow("room_area")),
                    roomDesc = cursor.getString(cursor.getColumnIndexOrThrow("room_desc"))
                )
                list.add(apt)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // ==========================================
    // Hàm để ADMIN cập nhật trạng thái căn hộ
    // ==========================================
    fun updateApartmentStatus(roomId: String, newStatus: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("room_status", newStatus) // Update đúng vào cột room_status

        // Cập nhật dòng có room_id tương ứng
        val result = db.update("Apartments", values, "room_id = ?", arrayOf(roomId))
        db.close()
        return result > 0 // Trả về true nếu cập nhật thành công
    }

    // Hàm lấy danh sách tất cả Hóa đơn cho ADMIN
    // ==========================================
    fun getAllInvoices(): List<Invoice> {
        val list = mutableListOf<Invoice>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Invoices ORDER BY invoice_id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val invoice = Invoice(
                    invoiceId = cursor.getInt(cursor.getColumnIndexOrThrow("invoice_id")),
                    roomId = cursor.getString(cursor.getColumnIndexOrThrow("room_id")),
                    invoiceTitle = cursor.getString(cursor.getColumnIndexOrThrow("invoice_title")),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    createdDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
                list.add(invoice)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Hàm tạo Hóa đơn mới
    // ==========================================
    fun insertInvoice(roomId: String, title: String, amount: Double, createdDate: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("room_id", roomId)
        values.put("invoice_title", title)
        values.put("amount", amount)
        values.put("created_date", createdDate)
        values.put("status", "Chưa thanh toán") // Mặc định khi mới tạo là chưa thanh toán

        val result = db.insert("Invoices", null, values)
        db.close()
        return result != -1L // Trả về true nếu lưu thành công
    }

    // Hàm để ADMIN cập nhật trạng thái Hóa đơn
    fun updateInvoiceStatus(invoiceId: Int, newStatus: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("status", newStatus)

        val result = db.update("Invoices", values, "invoice_id = ?", arrayOf(invoiceId.toString()))
        db.close()
        return result > 0
    }

    // Hàm cập nhật toàn bộ thông tin hóa đơn (Dùng khi chỉnh sửa chi tiết)
    fun updateFullInvoice(invoice: Invoice): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("room_id", invoice.roomId)
        values.put("invoice_title", invoice.invoiceTitle)
        values.put("amount", invoice.amount)
        values.put("created_date", invoice.createdDate)
        values.put("status", invoice.status)

        val result = db.update("Invoices", values, "invoice_id = ?", arrayOf(invoice.invoiceId.toString()))
        db.close()
        return result > 0
    }

    // Hàm xóa hóa đơn theo ID
    fun deleteInvoice(invoiceId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("Invoices", "invoice_id = ?", arrayOf(invoiceId.toString()))
        db.close()
        return result > 0
    }


    // ==========================================
    // Hàm lấy danh sách Hóa đơn cho USER (Theo mã phòng)
    // ==========================================
    fun getInvoicesByRoom(roomId: String): List<Invoice> {
        val list = mutableListOf<Invoice>()
        val db = this.readableDatabase
        // Chỉ lấy hóa đơn có mã phòng khớp với tham số truyền vào
        val cursor = db.rawQuery("SELECT * FROM Invoices WHERE room_id = ? ORDER BY invoice_id DESC", arrayOf(roomId))

        if (cursor.moveToFirst()) {
            do {
                val invoice = Invoice(
                    invoiceId = cursor.getInt(cursor.getColumnIndexOrThrow("invoice_id")),
                    roomId = cursor.getString(cursor.getColumnIndexOrThrow("room_id")),
                    invoiceTitle = cursor.getString(cursor.getColumnIndexOrThrow("invoice_title")),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    createdDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
                list.add(invoice)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // ==========================================
    // Hàm đếm số Hóa đơn CHƯA THANH TOÁN của 1 phòng
    // ==========================================
    fun getUnpaidInvoiceCount(roomId: String): Int {
        val db = this.readableDatabase
        var count = 0
        // Đếm số dòng thỏa mãn 2 điều kiện: đúng phòng đó VÀ trạng thái là chưa thanh toán
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Invoices WHERE room_id = ? AND status = 'Chưa thanh toán'", arrayOf(roomId))

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // ==========================================
    // Hàm lấy danh sách tất cả CƯ DÂN 
    // ==========================================
    fun getAllUsers(): List<User> {
        val list = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Users ORDER BY user_id ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("user_fullname")),
                    phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("user_phonenumber")),
                    roomID = cursor.getString(cursor.getColumnIndexOrThrow("user_roomID"))
                )
                list.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
