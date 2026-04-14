package com.example.qlcc

import java.io.Serializable

data class User(
    val userId: Int,
    val userName: String,
    val fullName: String,
    val phoneNumber: String,
    val roomID: String
) : Serializable