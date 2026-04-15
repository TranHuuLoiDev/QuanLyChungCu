package com.example.qlcc

import java.io.Serializable

data class Report(
    val reportId: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val createdDate: String,
    val status: String
) : Serializable