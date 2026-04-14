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
            // Mở file từ thư mục assets
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

    // Hàm kiểm tra Đăng nhập
    fun checkLogin(username: String, pass: String): User? {
        val db = this.readableDatabase
        var user: User? = null

        // Truy vấn tìm tài khoản trong bảng Users
        val cursor = db.rawQuery("SELECT * FROM Users WHERE user_name = ? AND user_password = ?", arrayOf(username, pass))

        if (cursor.moveToFirst()) {
            // Nếu tìm thấy, đóng gói thông tin vào Model User
            user = User(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow("user_fullname")),
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("user_phonenumber")),
                roomID = cursor.getString(cursor.getColumnIndexOrThrow("user_roomID"))
            )
        }
        cursor.close()
        db.close()
        return user
    }
}