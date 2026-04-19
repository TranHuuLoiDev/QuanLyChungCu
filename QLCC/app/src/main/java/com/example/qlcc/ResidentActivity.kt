package com.example.qlcc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ResidentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnThem: FloatingActionButton
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ResidentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resident_list)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() 
        }

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnThem = findViewById(R.id.btnThem)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadResidentList()

        btnThem.setOnClickListener {
            showAddResidentDialog()
        }
    }

    private fun showAddResidentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_resident, null)
        val edtUsername = dialogView.findViewById<EditText>(R.id.edtUsername)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edtPassword)
        val edtFullName = dialogView.findViewById<EditText>(R.id.edtFullName)
        val edtPhoneNumber = dialogView.findViewById<EditText>(R.id.edtPhoneNumber)
        val spnRoomID = dialogView.findViewById<Spinner>(R.id.spnRoomID)

        // Lấy danh sách phòng từ DB và kiểm tra trạng thái
        val apartments = dbHelper.getAllApartments()
        val displayList = apartments.map { apt ->
            val status = if (dbHelper.isRoomOccupied(apt.roomId)) "(Có người ở)" else "(Phòng trống)"
            "${apt.roomId} $status"
        }
        
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRoomID.adapter = spinnerAdapter

        AlertDialog.Builder(this)
            .setTitle("Thêm cư dân mới")
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val username = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()
                val fullName = edtFullName.text.toString().trim()
                val phone = edtPhoneNumber.text.toString().trim()
                
                // Tách lấy mã phòng thực sự từ chuỗi hiển thị
                val selectedItem = spnRoomID.selectedItem?.toString() ?: ""
                val roomID = selectedItem.split(" ")[0]

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty() || roomID.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else if (dbHelper.isRoomOccupied(roomID)) {
                    Toast.makeText(this, "Phòng $roomID này đã có người ở", Toast.LENGTH_SHORT).show()
                } else {
                    val success = dbHelper.insertUser(username, password, fullName, phone, roomID)
                    if (success) {
                        Toast.makeText(this, "Thêm cư dân thành công", Toast.LENGTH_SHORT).show()
                        loadResidentList()
                    } else {
                        Toast.makeText(this, "Lỗi khi thêm cư dân", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }

    private fun showEditResidentDialog(user: User) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_resident, null)
        val edtUsername = dialogView.findViewById<EditText>(R.id.edtUsername)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edtPassword)
        val edtFullName = dialogView.findViewById<EditText>(R.id.edtFullName)
        val edtPhoneNumber = dialogView.findViewById<EditText>(R.id.edtPhoneNumber)
        val spnRoomID = dialogView.findViewById<Spinner>(R.id.spnRoomID)

        edtUsername.setText(user.userName)
        edtUsername.isEnabled = false
        edtPassword.visibility = View.GONE

        edtFullName.setText(user.fullName)
        edtPhoneNumber.setText(user.phoneNumber)

        // Load Spinner với thông tin trạng thái
        val apartments = dbHelper.getAllApartments()
        val displayList = apartments.map { apt ->
            val status = if (dbHelper.isRoomOccupied(apt.roomId)) "(Có người ở)" else "(Phòng trống)"
            "${apt.roomId} $status"
        }
        
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRoomID.adapter = spinnerAdapter
        
        // Chọn đúng phòng hiện tại
        val position = apartments.indexOfFirst { it.roomId == user.roomID }
        if (position >= 0) {
            spnRoomID.setSelection(position)
        }

        AlertDialog.Builder(this)
            .setTitle("Chỉnh sửa cư dân")
            .setView(dialogView)
            .setPositiveButton("Cập nhật") { _, _ ->
                val fullName = edtFullName.text.toString().trim()
                val phone = edtPhoneNumber.text.toString().trim()
                
                val selectedItem = spnRoomID.selectedItem?.toString() ?: ""
                val roomID = selectedItem.split(" ")[0]

                if (fullName.isEmpty() || phone.isEmpty() || roomID.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else {
                    if (roomID != user.roomID && dbHelper.isRoomOccupied(roomID)) {
                        Toast.makeText(this, "Phòng này đã có người ở", Toast.LENGTH_SHORT).show()
                    } else {
                        val updatedUser = User(user.userId, user.userName, fullName, phone, roomID)
                        val success = dbHelper.updateUser(updatedUser)
                        if (success) {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                            loadResidentList()
                        } else {
                            Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }

    private fun loadResidentList() {
        val list = dbHelper.getAllUsers()
        adapter = ResidentAdapter(list,
            onEditClick = { user ->
                showEditResidentDialog(user)
            },
            onDeleteClick = { user ->
                showDeleteConfirmDialog(user)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun showDeleteConfirmDialog(user: User) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận xóa")
        builder.setMessage("Bạn có chắc chắn muốn xóa cư dân ${user.fullName}?")
        builder.setPositiveButton("Xóa") { _, _ ->
            val success = dbHelper.deleteUser(user.userId)
            if (success) {
                Toast.makeText(this, "Đã xóa cư dân", Toast.LENGTH_SHORT).show()
                loadResidentList()
            } else {
                Toast.makeText(this, "Lỗi khi xóa cư dân", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }
}
