package com.example.appql_chungcu

data class Apartment(
    val id: String,           // "101", "102", "201"...
    val name: String,         // "Căn hộ 101"
    val info: String,         // "2PN • 78m²"
    val status: String,       // "Còn trống" / "Đang cho thuê"
    val imageRes: Int = R.drawable.ic_apartment
)