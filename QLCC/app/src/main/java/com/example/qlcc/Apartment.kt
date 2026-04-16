package com.example.qlcc

data class Apartment(
    val id: String,
    val name: String,
    val info: String,
    val status: String,
    val imageRes: Int = R.drawable.apartment100
)