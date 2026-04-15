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
        // Lưu ý: Không nên để db.close() ở đây để tránh lỗi sập app khi màn hình khác muốn gọi Database
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
}