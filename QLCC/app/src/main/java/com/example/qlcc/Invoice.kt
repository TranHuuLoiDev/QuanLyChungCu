package com.example.qlcc

import java.io.Serializable

data class Invoice(
    val invoiceId: Int,
    val roomId: String,
    val invoiceTitle: String,
    val amount: Double,
    val createdDate: String,
    val status: String
) : Serializable