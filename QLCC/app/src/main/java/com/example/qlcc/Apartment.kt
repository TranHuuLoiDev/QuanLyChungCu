package com.example.qlcc
data class Apartment(
    val roomId: String,      // room_id từ database (ví dụ: A-101)
    val area: String?,       // room_area
    val status: String?,     // room_status
    val desc: String?,       // room_desc
    val imageRes: Int = R.drawable.apartment100   // ảnh mặc định
) {
    // Tạo tên hiển thị từ room_id
    val name: String get() = "Căn hộ $roomId"
}