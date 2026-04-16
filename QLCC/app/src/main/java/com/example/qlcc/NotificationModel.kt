package com.example.qlcc

data class NotificationModel(
    val notifyId: Int,
    val title: String,
    val content: String,
    val type: String,
    val createdAt: String
)