package com.example.qlcc

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    // 2. Hàm kiểm tra Đăng nhập cho ADMIN
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
    // 3. QUẢN LÝ PHẢN ÁNH
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

    fun insertReport(userId: Int, title: String, content: String, date: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("user_id", userId)
        values.put("title", title)
        values.put("content", content)
        values.put("created_date", date)
        values.put("status", "Chờ xử lý")

        val result = db.insert("Reports", null, values)
        db.close()
        return result != -1L
    }

    // ==========================================
    // 4. QUẢN LÝ CĂN HỘ
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

    fun updateApartmentStatus(roomId: String, newStatus: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("room_status", newStatus)

        val result = db.update("Apartments", values, "room_id = ?", arrayOf(roomId))
        db.close()
        return result > 0
    }

    // ==========================================
    // 5. QUẢN LÝ HÓA ĐƠN
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

    fun insertInvoice(roomId: String, title: String, amount: Double, createdDate: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("room_id", roomId)
        values.put("invoice_title", title)
        values.put("amount", amount)
        values.put("created_date", createdDate)
        values.put("status", "Chưa thanh toán")

        val result = db.insert("Invoices", null, values)
        db.close()
        return result != -1L
    }

    fun updateInvoiceStatus(invoiceId: Int, newStatus: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("status", newStatus)

        val result = db.update("Invoices", values, "invoice_id = ?", arrayOf(invoiceId.toString()))
        db.close()
        return result > 0
    }

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

    fun deleteInvoice(invoiceId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("Invoices", "invoice_id = ?", arrayOf(invoiceId.toString()))
        db.close()
        return result > 0
    }

    fun getInvoicesByRoom(roomId: String): List<Invoice> {
        val list = mutableListOf<Invoice>()
        val db = this.readableDatabase
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

    fun getUnpaidInvoiceCount(roomId: String): Int {
        val db = this.readableDatabase
        var count = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Invoices WHERE room_id = ? AND status = 'Chưa thanh toán'", arrayOf(roomId))

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // ==========================================
    // 6. QUẢN LÝ CƯ DÂN
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

    fun insertUser(userName: String, pass: String, fullName: String, phone: String, roomID: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("user_name", userName)
        values.put("user_password", pass)
        values.put("user_fullname", fullName)
        values.put("user_phonenumber", phone)
        values.put("user_roomID", roomID)

        val result = db.insert("Users", null, values)
        db.close()
        return result != -1L
    }

    fun updateUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("user_fullname", user.fullName)
        values.put("user_phonenumber", user.phoneNumber)
        values.put("user_roomID", user.roomID)

        val result = db.update("Users", values, "user_id = ?", arrayOf(user.userId.toString()))
        db.close()
        return result > 0
    }

    fun deleteUser(userId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("Users", "user_id = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }
     // Hàm kiểm tra phòng đã có người ở chưa
     fun isRoomOccupied(roomID: String): Boolean {
         val db = this.readableDatabase
         val cursor = db.rawQuery("SELECT 1 FROM Users WHERE user_roomID = ?", arrayOf(roomID))
         val occupied = cursor.count > 0
         cursor.close()
         return occupied
    }
    // ==========================================
    // 7. QUẢN LÝ THÔNG BÁO (ADMIN & USER)
    // ==========================================

    // Lấy tất cả thông báo (Cho màn hình Danh sách)
    fun getAllNotifications(): List<NotificationModel> {
        val list = mutableListOf<NotificationModel>()
        val db = this.readableDatabase
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

    // Lấy 1 thông báo mới nhất (Cho trang chủ của User)
    fun getLatestNotification(): NotificationModel? {
        val db = this.readableDatabase
        var notification: NotificationModel? = null
        val cursor = db.rawQuery("SELECT * FROM Notifications ORDER BY notify_id DESC LIMIT 1", null)

        if (cursor.moveToFirst()) {
            notification = NotificationModel(
                notifyId = cursor.getInt(cursor.getColumnIndexOrThrow("notify_id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
        }
        cursor.close()
        return notification
    }

    // Admin tạo thông báo mới
    fun insertNotification(adminId: Int, title: String, content: String, type: String, createdAt: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("admin_id", adminId)
        values.put("title", title)
        values.put("content", content)
        values.put("type", type)
        values.put("created_at", createdAt)

        val result = db.insert("Notifications", null, values)
        db.close()
        return result != -1L
    }

    // Admin sửa thông báo
    fun updateNotification(notifyId: Int, title: String, content: String, type: String): Boolean {
        val db = this.writableDatabase
        val values = android.content.ContentValues()
        values.put("title", title)
        values.put("content", content)
        values.put("type", type)

        val result = db.update("Notifications", values, "notify_id = ?", arrayOf(notifyId.toString()))
        db.close()
        return result > 0
    }

    // Admin xóa thông báo
    fun deleteNotification(notifyId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("Notifications", "notify_id = ?", arrayOf(notifyId.toString()))
        db.close()
        return result > 0
    }
}
