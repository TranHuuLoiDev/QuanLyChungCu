package com.example.qlcc
import java.io.Serializable

// PHẢI có chữ "data" ở phía trước
data class Report(
    val reportId: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val createdDate: String,
    var status: String // Để var để dễ cập nhật
) : Serializable
