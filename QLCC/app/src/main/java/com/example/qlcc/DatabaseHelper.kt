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

    // Lấy danh sách căn hộ theo tầng từ database
    fun getApartmentsByFloor(floor: Int): List<Apartment> {
        val list = mutableListOf<Apartment>()
        val db = this.readableDatabase

        try {
            val cursor = db.rawQuery(
                "SELECT * FROM Apartments WHERE room_id LIKE ? ORDER BY room_id ASC",
                arrayOf("$floor%")
            )

            if (cursor.moveToFirst()) {
                do {
                    val apartment = Apartment(
                        roomId = cursor.getString(cursor.getColumnIndexOrThrow("room_id")),
                        area = cursor.getString(cursor.getColumnIndexOrThrow("room_area")),
                        status = cursor.getString(cursor.getColumnIndexOrThrow("room_status")),
                        desc = cursor.getString(cursor.getColumnIndexOrThrow("room_desc"))
                    )
                    list.add(apartment)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return list
    }
}